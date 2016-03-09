package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.Collection;
import java.util.Collections;

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

    public TableMetadataProducer( DatabaseClient databaseClient )
    {
        this.databaseClient = databaseClient;
    }

    @Override
    public Collection<Table> createMetadataFor( TableName source ) throws Exception
    {
        String sql = "SELECT " +
                "COLUMN_NAME, " +
                "DATA_TYPE, " +
                "COLUMN_KEY " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = '" + source.schema() +
                "' AND TABLE_NAME ='" + source.simpleName() + "';";

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

                builder.addColumn(
                        new Column(
                                source,
                                source.fullyQualifiedColumnName( columnName ),
                                columnName,
                                columnType,
                                dataType ) );
            }
        }

        return Collections.singletonList( builder.build() );
    }
}
