package org.neo4j.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.CompletableFuture;

import org.neo4j.utils.FutureUtils;

public class SqlRunner implements AutoCloseable
{
    private final String sql;
    private volatile boolean allowContinue = true;

    public SqlRunner( String sql )
    {
        this.sql = sql;
    }

    public void terminate()
    {
        allowContinue = false;
    }

    public CompletableFuture<Void> execute()
    {
        return FutureUtils.exceptionableFuture( () ->
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

            return null;

        } );
    }

    @Override
    public void close() throws Exception
    {
        terminate();
    }
}
