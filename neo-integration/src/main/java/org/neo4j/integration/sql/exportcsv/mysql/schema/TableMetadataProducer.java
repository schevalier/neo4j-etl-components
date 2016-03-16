package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlDataType;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.MetadataProducer;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;

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
            while ( results.next() )
            {
                ColumnType columnType = ColumnType.valueOf( results.getString( "COLUMN_TYPE" ));

                if ( columnFilter.test( columnType ) )
                {
                    String columnName = results.getString( "COLUMN_NAME" );
                    SqlDataType dataType = MySqlDataType.parse( results.getString( "DATA_TYPE" ) );

                    builder.addColumn(
                            new Column(
                                    source,
                                    source.fullyQualifiedColumnName( columnName ),
                                    columnName,
                                    columnType,
                                    dataType ) );
                }
            }
        }

        return Collections.singletonList( builder.build() );
    }
}
