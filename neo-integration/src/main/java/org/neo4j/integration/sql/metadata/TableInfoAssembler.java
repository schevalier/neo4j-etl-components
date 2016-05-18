package org.neo4j.integration.sql.metadata;

import java.util.ArrayList;
import java.util.Collection;
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
                    .map( pk -> new SimpleColumn(
                            table,
                            pk.get( "COLUMN_NAME" ),
                            ColumnRole.PrimaryKey,
                            columnTypes.getSqlDataType( pk.get( "COLUMN_NAME" ) ) ) )
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
                return Optional.of( new CompositeColumn( table, columns ) );
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

                foreignKeyGroup.forEach( fk -> {
                    sourceColumns.add( new SimpleColumn(
                            table,
                            fk.get( "FKCOLUMN_NAME" ),
                            ColumnRole.ForeignKey,
                            columnTypes.getSqlDataType( fk.get( "FKCOLUMN_NAME" ) ) ) );
                    TableName targetTableName = new TableName(
                            firstNonNullOrEmpty( fk.get( "PKTABLE_CAT" ), fk.get( "PKTABLE_SCHEM" ) ),
                            fk.get( "PKTABLE_NAME" ) );
                    targetColumns.add( new SimpleColumn(
                            targetTableName,
                            fk.get( "PKCOLUMN_NAME" ),
                            ColumnRole.PrimaryKey,
                            columnTypes.getSqlDataType( fk.get( "FKCOLUMN_NAME" ) ) ) );
                } );

                if ( sourceColumns.size() == 1 )
                {
                    keys.add( new JoinKey( sourceColumns.get( 0 ), targetColumns.get( 0 ) ) );
                }
                else
                {
                    keys.add( new JoinKey(
                            new CompositeColumn( table, sourceColumns ),
                            new CompositeColumn( targetColumns.get( 0 ).table(), targetColumns ) ) );
                }
            }

            return keys;
        }
    }

    private String firstNonNullOrEmpty( String a, String b )
    {
        return StringUtils.isNotEmpty( a ) ? a : b;
    }

    private static class ColumnTypes
    {
        private final Map<String, String> columnTypes;

        private ColumnTypes( Map<String, String> columnTypes )
        {
            this.columnTypes = columnTypes;
        }

        SqlDataType getSqlDataType( String column )
        {
            return SqlDataType.parse( columnTypes.get( column ) );
        }

        Collection<Column> toColumns( TableName table )
        {
            return columnTypes.entrySet().stream()
                    .map( e ->
                            new SimpleColumn( table, e.getKey(), ColumnRole.Data, SqlDataType.parse( e.getValue() ) ) )
                    .filter( c -> !c.sqlDataType().skipImport() )
                    .collect( Collectors.toList() );
        }

    }
}
