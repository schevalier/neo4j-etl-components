package org.neo4j.integration.sql.exportcsv.mysql.schema;

import org.neo4j.integration.sql.Results;
import org.neo4j.integration.sql.SqlRunner;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;

public class GetTableMetadata
{
    private final SqlRunner sqlRunner;

    public GetTableMetadata( SqlRunner sqlRunner )
    {
        this.sqlRunner = sqlRunner;
    }

    public Table getMetadataFor( TableName tableName ) throws Exception
    {
        String sql = "SELECT " +
                "COLUMN_NAME, " +
                "DATA_TYPE, " +
                "COLUMN_KEY " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = '" + tableName.schema() +
                "' AND TABLE_NAME ='" + tableName.simpleName() + "';";

        Table.Builder builder = Table.builder().name( tableName );

        try ( Results results = sqlRunner.execute( sql ).await() )
        {
            while ( results.next() )
            {
                String columnName = results.getString( "COLUMN_NAME" );
                String columnKey = results.getString( "COLUMN_KEY" );

                switch ( columnKey )
                {
                    case "PRI":
                        builder.addColumn( columnName, ColumnType.PrimaryKey );
                        break;
                    case "MUL":
                        builder.addColumn( columnName, ColumnType.ForeignKey );
                        break;
                    default:
                        builder.addColumn( columnName, ColumnType.Data );
                        break;
                }
            }
        }

        return builder.build();
    }

}
