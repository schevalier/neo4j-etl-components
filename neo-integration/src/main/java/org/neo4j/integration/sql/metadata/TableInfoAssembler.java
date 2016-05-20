package org.neo4j.integration.sql.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.util.SqlDataTypeUtils;

import static java.lang.String.format;

public class TableInfoAssembler
{
    private final DatabaseClient databaseClient;

    public TableInfoAssembler( DatabaseClient databaseClient )
    {
        this.databaseClient = databaseClient;
    }

    public TableInfo createTableInfo( TableName tableName ) throws Exception
    {
        Collection<Column> keyColumns = new HashSet<>();

        Map<String, Column> allColumns = createColumnsMap( tableName );
        Optional<Column> primaryKey = createPrimaryKey( tableName, allColumns, keyColumns );
        Collection<JoinKey> foreignKeys = createForeignKeys( tableName, allColumns, keyColumns );

        return new TableInfo(
                tableName,
                primaryKey,
                foreignKeys,
                columnsLessKeyColumns( allColumns, keyColumns ) );
    }

    private List<Column> columnsLessKeyColumns( Map<String, Column> allColumns, Collection<Column> keyColumns )
    {
        return allColumns.values().stream().filter( c -> !keyColumns.contains( c ) ).collect( Collectors.toList() );
    }

    private Map<String, Column> createColumnsMap( TableName tableName ) throws Exception
    {
        try ( QueryResults columnsResults = databaseClient.columns( tableName ) )
        {
            return columnsResults.stream()
                    .map( m -> new String[]{m.get( "COLUMN_NAME" ), m.get( "TYPE_NAME" )} )
                    .collect( Collectors.toMap( v -> v[0], v -> v[1] ) )
                    .entrySet().stream()
                    .map( e -> new SimpleColumn(
                            tableName,
                            e.getKey(),
                            EnumSet.of( ColumnRole.Data ),
                            SqlDataTypeUtils.parse( e.getValue() ),
                            ColumnValueSelectionStrategy.SelectColumnValue ) )
                    .filter( c -> !c.sqlDataType().skipImport() )
                    .collect( Collectors.toMap( Column::name, c -> c ) );
        }
    }

    private Optional<Column> createPrimaryKey( TableName table,
                                               Map<String, Column> columns,
                                               Collection<Column> keyColumns ) throws Exception
    {
        try ( QueryResults primaryKeyResults = databaseClient.primaryKeys( table ) )
        {
            List<Column> primaryKeyColumns = primaryKeyResults.stream()
                    .map( pk -> columns.get( table.fullyQualifiedColumnName( pk.get( "COLUMN_NAME" ) ) ) )
                    .collect( Collectors.toList() );

            keyColumns.addAll( primaryKeyColumns );

            return primaryKeyColumns.isEmpty() ?
                    Optional.empty() :
                    Optional.of( new CompositeColumn( table, primaryKeyColumns, EnumSet.of( ColumnRole.PrimaryKey ) ) );
        }
    }

    private Collection<JoinKey> createForeignKeys( TableName table,
                                                   Map<String, Column> columns,
                                                   Collection<Column> keyColumns ) throws Exception
    {
        try ( QueryResults foreignKeysResults = databaseClient.foreignKeys( table ) )
        {
            Map<String, List<Map<String, String>>> foreignKeyGroups = foreignKeysResults.stream()
                    .collect( Collectors.groupingBy( r -> r.get( "FK_NAME" ) ) );

            Collection<JoinKey> keys = new ArrayList<>();

            for ( List<Map<String, String>> foreignKeyGroup : foreignKeyGroups.values() )
            {
                List<Column> sourceColumns = new ArrayList<>();
                List<Column> targetColumns = new ArrayList<>();

                foreignKeyGroup.forEach( fk ->
                {
                    Column sourceColumn = columns.get( table.fullyQualifiedColumnName( fk.get( "FKCOLUMN_NAME" ) ) );
                    // We assume the key's target column data type is the same as the source column's data type
                    SqlDataType sqlDataType = sourceColumn.sqlDataType();

                    sourceColumns.add( sourceColumn );
                    targetColumns.add( createForeignKeyTargetColumn( fk, sqlDataType ) );
                } );

                keyColumns.addAll( sourceColumns );

                // We assume that for a composite foreign key, all parts of the key refer to the same target table
                if ( targetColumns.stream().map( Column::table ).collect( Collectors.toSet() ).size() > 1 )
                {
                    throw new IllegalStateException(
                            format( "Composite foreign key refers to more than one target table: %s",
                                    foreignKeyGroup ) );
                }

                TableName targetTable = targetColumns.get( 0 ).table();
                keys.add( new JoinKey(
                        new CompositeColumn( table, sourceColumns, EnumSet.of( ColumnRole.ForeignKey ) ),
                        new CompositeColumn( targetTable, targetColumns, EnumSet.of( ColumnRole.PrimaryKey ) ) ) );
            }

            return keys;
        }
    }

    private Column createForeignKeyTargetColumn( Map<String, String> fk, SqlDataType sqlDataType )
    {
        TableName targetTableName = new TableName(
                firstNonNullOrEmpty( fk.get( "PKTABLE_CAT" ), fk.get( "PKTABLE_SCHEM" ) ),
                fk.get( "PKTABLE_NAME" ) );

        return new SimpleColumn(
                targetTableName,
                fk.get( "PKCOLUMN_NAME" ),
                EnumSet.of( ColumnRole.Data ),
                sqlDataType, ColumnValueSelectionStrategy.SelectColumnValue );
    }

    private String firstNonNullOrEmpty( String a, String b )
    {
        return StringUtils.isNotEmpty( a ) ? a : b;
    }
}
