package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.integration.sql.Results;
import org.neo4j.integration.sql.SqlRunner;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.TableName;

public class GetJoinMetadata
{
    private final SqlRunner sqlRunner;

    public GetJoinMetadata( SqlRunner sqlRunner )
    {
        this.sqlRunner = sqlRunner;
    }

    public Collection<Join> getMetadataFor( TableName t1, TableName t2 ) throws Exception
    {
        String sql = select( t1, t2 ) + " UNION " + select( t2, t1 );

        Collection<Join> joins = new ArrayList<>();

        try ( Results results = sqlRunner.execute( sql ).await() )
        {
            while ( results.next() )
            {
                Join join = Join.builder()
                        .parentTable(
                                new TableName(
                                        results.getString( "TABLE_SCHEMA" ),
                                        results.getString( "TABLE_NAME" ) ) )
                        .primaryKey( results.getString( "PRIMARY_KEY" ) )
                        .foreignKey( results.getString( "FOREIGN_KEY" ) )
                        .childTable(
                                new TableName(
                                        results.getString( "REFERENCED_TABLE_SCHEMA" ),
                                        results.getString( "REFERENCED_TABLE_NAME" ) ) )
                        .build();

                joins.add( join );
            }
        }

        return joins;
    }

    private String select( TableName t1, TableName t2 )
    {
        return "SELECT " +
                " kcu.TABLE_SCHEMA," +
                " kcu.TABLE_NAME," +
                " c3.COLUMN_NAME AS PRIMARY_KEY," +
                " kcu.COLUMN_NAME AS FOREIGN_KEY," +
                " c1.DATA_TYPE AS COLUMN_DATA_TYPE," +
                " kcu.REFERENCED_COLUMN_NAME AS REFERENCED_PRIMARY_KEY," +
                " c2.DATA_TYPE AS REFERENCED_COLUMN_DATA_TYPE," +
                " kcu.REFERENCED_TABLE_SCHEMA," +
                " kcu.REFERENCED_TABLE_NAME " +
                "FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE AS kcu " +
                "INNER JOIN INFORMATION_SCHEMA.COLUMNS AS c1 ON " +
                " (kcu.TABLE_SCHEMA = c1.TABLE_SCHEMA AND " +
                " kcu.TABLE_NAME = c1.TABLE_NAME " +
                " AND kcu.COLUMN_NAME = c1.COLUMN_NAME) " +
                "INNER JOIN INFORMATION_SCHEMA.COLUMNS AS c2 ON " +
                " (kcu.TABLE_SCHEMA = c2.TABLE_SCHEMA " +
                " AND kcu.TABLE_NAME = c2.TABLE_NAME " +
                " AND kcu.COLUMN_NAME = c2.COLUMN_NAME) " +
                "INNER JOIN INFORMATION_SCHEMA.COLUMNS AS c3 ON " +
                " (kcu.TABLE_SCHEMA = c3.TABLE_SCHEMA " +
                " AND kcu.TABLE_NAME = c3.TABLE_NAME " +
                " AND c3.COLUMN_KEY = 'PRI') " +
                "WHERE kcu.TABLE_SCHEMA = '" + t1.schema() + "' " +
                " AND kcu.TABLE_NAME = '" + t1.simpleName() + "' " +
                " AND kcu.REFERENCED_TABLE_SCHEMA = '" + t2.schema() + "' " +
                " AND kcu.REFERENCED_TABLE_NAME = '" + t2.simpleName() + "'";
    }
}
