package org.neo4j.mysql;

import java.sql.Connection;
import java.sql.DriverManager;

class SqlRunner implements PipeReader
{
    private final String sql;
    private volatile boolean allowContinue = true;

    private volatile Exception ex;

    SqlRunner( String sql )
    {
        this.sql = sql;
    }

    public void terminate()
    {
        allowContinue = false;
    }

    @Override
    public void open()
    {
        String url = "jdbc:mysql://localhost:3306/javabase";
        String username = "java";
        String password = "password";

        System.out.println( "Connecting to database..." );

        try ( Connection connection = DriverManager.getConnection( url, username, password ) )
        {
            System.out.println( "Connected to database" );
            connection.createStatement().execute( sql );

            while ( allowContinue )
            {
                Thread.sleep( 100 );
            }
        }
        catch ( Exception e )
        {
            ex = e;
        }
    }

    @Override
    public void rethrow() throws Exception
    {
        if ( ex != null )
        {
            throw ex;
        }
    }

    @Override
    public void close() throws Exception
    {
        terminate();
        rethrow();
    }
}
