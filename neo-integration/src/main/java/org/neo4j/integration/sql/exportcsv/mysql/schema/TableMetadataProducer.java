package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlDataType;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.CompositeKeyColumn;
import org.neo4j.integration.sql.metadata.MetadataProducer;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class TableMetadataProducer implements MetadataProducer<TableName, Table>
{
    private final DatabaseClient databaseClient;
    private final Predicate<ColumnType> columnFilter;

    public TableMetadataProducer( DatabaseClient databaseClient )
    {
        this( databaseClient, c -> true );
    }

    public TableMetadataProducer( DatabaseClient databaseClient, Predicate<ColumnType> columnFilter )
    {
        this.databaseClient = databaseClient;
        this.columnFilter = columnFilter;
    }

    @Override
    public Collection<Table> createMetadataFor( TableName source ) throws Exception
    {
        String sql = "SELECT " +
                "c.COLUMN_NAME AS COLUMN_NAME," +
                "CASE (SELECT COUNT(kcu.REFERENCED_TABLE_NAME) " +
                "      FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu " +
                "      WHERE c.TABLE_SCHEMA = kcu.TABLE_SCHEMA " +
                "      AND c.TABLE_NAME = kcu.TABLE_NAME " +
                "      AND c.COLUMN_NAME = kcu.COLUMN_NAME )" +
                "    WHEN 0 THEN" +
                "      CASE c.COLUMN_KEY" +
                "        WHEN 'PRI' THEN 'PrimaryKey'" +
                "        ELSE 'Data'" +
                "      END" +
                "    ELSE 'ForeignKey'" +
                "END AS COLUMN_TYPE," +
                "c.DATA_TYPE AS DATA_TYPE " +
                "FROM INFORMATION_SCHEMA.COLUMNS c " +
                "WHERE c.TABLE_SCHEMA = '" + source.schema() + "' AND c.TABLE_NAME ='" + source.simpleName() + "';";

        Table.Builder builder = Table.builder().name( source );

        try ( QueryResults results = databaseClient.executeQuery( sql ).await() )
        {
            Map<String, List<Map<String, String>>> groupedByColumnType = results.stream()
                    .collect( groupingBy( row -> row.get( "COLUMN_TYPE" ) ) );
            groupedByColumnType.entrySet().stream()
                    .forEach( columnTypeRowAndValues -> addColumnsForColumnType( source, builder, columnTypeRowAndValues ) );
        }

        return Collections.singletonList( builder.build() );
    }

    private void addColumnsForColumnType( TableName source,
                                          Table.Builder builder,
                                          Map.Entry<String, List<Map<String, String>>> columnTypeRowAndValues )
    {
        if ( "PrimaryKey".equalsIgnoreCase( columnTypeRowAndValues.getKey() ) )
        {
            addColumnsForPrimaryKeys( source, builder, columnTypeRowAndValues );
        }
        else
        {
            columnTypeRowAndValues.getValue().stream().
                    forEach( row -> addSimpleColumn( source, builder, row ) );

        }
    }

    private void addColumnsForPrimaryKeys( TableName source, Table.Builder builder,
                                           Map.Entry<String, List<Map<String, String>>> columnTypeRow )
    {
        List<Map<String, String>> primaryKeyRows = columnTypeRow.getValue();
        if ( primaryKeyRows.size() > 1 )
        {
            addCompositeKey( source, builder, primaryKeyRows );
        }
        else
        {
            addSimpleColumn( source, builder,  primaryKeyRows.get( 0 ) );
        }
    }

    private void addSimpleColumn( TableName source, Table.Builder builder, Map<String, String> row )
    {
        Optional<SimpleColumn> columnOptional = buildSimpleColumn( source, row );
        if ( columnOptional.isPresent() )
        {
            builder.addColumn( columnOptional.get() );
        }
    }

    private void addCompositeKey( TableName source, Table.Builder builder, List<Map<String, String>> primaryKeyRows )
    {
        List<Column> primaryKeyColumns = primaryKeyRows.stream()
                .map( row -> buildSimpleColumn( source, row ) )
                .filter( Optional::isPresent )
                .map( Optional::get )
                .collect( toList() );
        builder.addColumn( new CompositeKeyColumn( source, primaryKeyColumns ) );
    }

    private Optional<SimpleColumn> buildSimpleColumn( TableName source, Map<String, String> row )
    {
        ColumnType columnType = ColumnType.valueOf( row.get( "COLUMN_TYPE" ) );
        if ( columnFilter.test( columnType ) )
        {
            String columnName = row.get( "COLUMN_NAME" );
            SqlDataType dataType = MySqlDataType.parse( row.get( "DATA_TYPE" ) );

            return Optional.of( new SimpleColumn(
                    source,
                    source.fullyQualifiedColumnName( columnName ),
                    columnName,
                    columnType,
                    dataType ) );
        }
        else
        {
            return Optional.empty();
        }
    }
}
