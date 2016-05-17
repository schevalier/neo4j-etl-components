package org.neo4j.integration.sql.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import org.neo4j.integration.sql.MySqlDatabaseClient;

class KeyCollectionAssembler
{
    private final MySqlDatabaseClient databaseClient;

    KeyCollectionAssembler( MySqlDatabaseClient databaseClient )
    {
        this.databaseClient = databaseClient;
    }

    KeyCollection createKeyCollection( TableName tableName ) throws Exception
    {
        Map<String, String> columnTypes = databaseClient.columns( tableName ).stream()
                .map( m -> new String[]{m.get( "COLUMN_NAME" ), m.get( "TYPE_NAME" )} )
                .collect( Collectors.toMap( v -> v[0], v -> v[1] ) );

        return new KeyCollection(
                createPrimaryKey( tableName, columnTypes ),
                createForeignKeys( tableName, columnTypes ),
                createColumns( tableName, columnTypes ) );
    }


    private Optional<Column> createPrimaryKey( TableName table, Map<String, String> columnTypes ) throws Exception
    {
        List<Map<String, String>> primaryKeys =
                databaseClient.primaryKeys( table ).stream().collect( Collectors.toList() );

        List<Column> columns = primaryKeys.stream()
                .map( pk -> new SimpleColumn(
                        table,
                        pk.get( "COLUMN_NAME" ),
                        ColumnRole.PrimaryKey,
                        SqlDataType.parse( columnTypes.get( pk.get( "COLUMN_NAME" ) ) ) ) )
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

    private Collection<JoinKey> createForeignKeys( TableName table, Map<String, String> columnTypes ) throws Exception
    {
        Map<String, List<Map<String, String>>> foreignKeys = databaseClient.foreignKeys( table ).stream()
                .collect( Collectors.groupingBy( r -> r.get( "FK_NAME" ) ) );

        Collection<JoinKey> keys = new ArrayList<>();

        for ( List<Map<String, String>> foreignKeyPart : foreignKeys.values() )
        {
            List<Column> sourceColumns = new ArrayList<>();
            List<Column> targetColumns = new ArrayList<>();

            foreignKeyPart.forEach( fk -> {
                sourceColumns.add( new SimpleColumn(
                        table,
                        fk.get( "FKCOLUMN_NAME" ),
                        ColumnRole.ForeignKey,
                        SqlDataType.parse( columnTypes.get( fk.get( "FKCOLUMN_NAME" ) ) ) ) );
                TableName targetTableName = new TableName(
                        firstNonNullOrEmpty( fk.get( "PKTABLE_CAT" ), fk.get( "PKTABLE_SCHEM" ) ),
                        fk.get( "PKTABLE_NAME" ) );
                targetColumns.add( new SimpleColumn(
                        targetTableName,
                        fk.get( "PKCOLUMN_NAME" ),
                        ColumnRole.PrimaryKey,
                        SqlDataType.parse( columnTypes.get( fk.get( "FKCOLUMN_NAME" ) ) ) ) );
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

    private Collection<Column> createColumns( TableName table, Map<String, String> columnTypes )
    {
        Collection<Column> columns = new ArrayList<>();

        columnTypes.entrySet().forEach( e -> columns.add(
                new SimpleColumn( table, e.getKey(), ColumnRole.Data, SqlDataType.parse( e.getValue() ) ) ) );

        return columns;
    }

    private String firstNonNullOrEmpty( String a, String b )
    {
        return StringUtils.isNotEmpty( a ) ? a : b;
    }
}
