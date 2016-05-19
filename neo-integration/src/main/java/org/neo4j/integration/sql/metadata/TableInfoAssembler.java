package org.neo4j.integration.sql.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;

public class TableInfoAssembler
{
    private final DatabaseClient databaseClient;

    public TableInfoAssembler( DatabaseClient databaseClient )
    {
        this.databaseClient = databaseClient;
    }

    public TableInfo createTableInfo( TableName tableName ) throws Exception
    {
        try ( QueryResults columnsResults = databaseClient.columns( tableName ) )
        {
            ColumnTypes columnTypes = new ColumnTypes( columnsResults.stream()
                    .map( m -> new String[]{m.get( "COLUMN_NAME" ), m.get( "TYPE_NAME" )} )
                    .collect( Collectors.toMap( v -> v[0], v -> v[1] ) ) );

            return new TableInfo(
                    tableName,
                    createPrimaryKey( tableName, columnTypes ),
                    createForeignKeys( tableName, columnTypes ),
                    columnTypes.toColumns( tableName ) );
        }
    }

    private Optional<Column> createPrimaryKey( TableName table, ColumnTypes columnTypes ) throws Exception
    {
        try ( QueryResults primaryKeyResults = databaseClient.primaryKeys( table ) )
        {
            List<Column> columns = primaryKeyResults.stream()
                    .map( pk -> createPrimaryKeyColumn( table, columnTypes, pk ) )
                    .collect( Collectors.toList() );

            if ( columns.isEmpty() )
            {
                return Optional.empty();
            }
            else if ( columns.size() == 1 )
            {
                return Optional.of( columns.get( 0 ) );
            }
            else
            {
                return Optional.of( new CompositeColumn( table, columns, EnumSet.of( ColumnRole.PrimaryKey ) ) );
            }
        }
    }

    private Collection<JoinKey> createForeignKeys( TableName table, ColumnTypes columnTypes ) throws Exception
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
                    sourceColumns.add( createForeignKeySourceColumn( table, columnTypes, fk ) );
                    targetColumns.add( createForeignKeyTargetColumn( columnTypes, fk ) );
                } );

                if ( sourceColumns.size() == 1 )
                {
                    keys.add( new JoinKey( sourceColumns.get( 0 ), targetColumns.get( 0 ) ) );
                }
                else
                {
                    TableName targetTable = targetColumns.get( 0 ).table();
                    keys.add( new JoinKey(
                            new CompositeColumn( table, sourceColumns, EnumSet.of( ColumnRole.ForeignKey ) ),
                            new CompositeColumn( targetTable, targetColumns, EnumSet.of( ColumnRole.PrimaryKey ) ) ) );
                }
            }

            return keys;
        }
    }

    private Column createPrimaryKeyColumn( TableName table, ColumnTypes columnTypes, Map<String, String> pk )
    {
        return new SimpleColumn(
                table,
                pk.get( "COLUMN_NAME" ),
                EnumSet.of( ColumnRole.PrimaryKey ),
                columnTypes.getSqlDataType( pk.get( "COLUMN_NAME" ) ) );
    }

    private Column createForeignKeySourceColumn( TableName table, ColumnTypes columnTypes, Map<String, String> fk )
    {
        return new SimpleColumn(
                table,
                fk.get( "FKCOLUMN_NAME" ),
                EnumSet.of( ColumnRole.ForeignKey ),
                columnTypes.getSqlDataType( fk.get( "FKCOLUMN_NAME" ) ) );
    }

    private Column createForeignKeyTargetColumn( ColumnTypes columnTypes, Map<String, String> fk )
    {
        TableName targetTableName = new TableName(
                firstNonNullOrEmpty( fk.get( "PKTABLE_CAT" ), fk.get( "PKTABLE_SCHEM" ) ),
                fk.get( "PKTABLE_NAME" ) );

        return new SimpleColumn(
                targetTableName,
                fk.get( "PKCOLUMN_NAME" ),
                EnumSet.of( ColumnRole.PrimaryKey ),
                columnTypes.getSqlDataType( fk.get( "FKCOLUMN_NAME" ) ) );
    }

    private String firstNonNullOrEmpty( String a, String b )
    {
        return StringUtils.isNotEmpty( a ) ? a : b;
    }
}
