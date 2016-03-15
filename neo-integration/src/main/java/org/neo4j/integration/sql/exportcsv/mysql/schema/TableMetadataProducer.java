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
                "        WHEN 'PRI' THEN 'PRI'" +
                "        ELSE ''" +
                "      END" +
                "    ELSE 'MUL'" +
                "END AS COLUMN_KEY," +
                "c.DATA_TYPE AS DATA_TYPE " +
                "FROM INFORMATION_SCHEMA.COLUMNS c " +
                "WHERE c.TABLE_SCHEMA = '" + source.schema() + "' AND c.TABLE_NAME ='" + source.simpleName() + "';";

        Table.Builder builder = Table.builder().name( source );

        try ( QueryResults results = databaseClient.executeQuery( sql ).await() )
        {
            while ( results.next() )
            {
                String columnName = results.getString( "COLUMN_NAME" );
                String columnKey = results.getString( "COLUMN_KEY" );
                SqlDataType dataType = MySqlDataType.parse( results.getString( "DATA_TYPE" ) );

                ColumnType columnType;

                switch ( columnKey )
                {
                    case "PRI":
                        columnType = ColumnType.PrimaryKey;
                        break;
                    case "MUL":
                        columnType = ColumnType.ForeignKey;
                        break;
                    default:
                        columnType = ColumnType.Data;
                        break;
                }

                if ( columnFilter.test( columnType ) )
                {
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
