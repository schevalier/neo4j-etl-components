package org.neo4j.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.CompletableFuture;

import org.neo4j.utils.FutureUtils;
import org.neo4j.utils.Loggers;

public class SqlRunner
{
    private final String sql;

    public SqlRunner( String sql )
    {
        this.sql = sql;
    }

    public CompletableFuture<Void> execute()
    {
        return FutureUtils.exceptionableFuture( () ->
        {
            String url = "jdbc:mysql://localhost:3306/javabase";
            String username = "java";
            String password = "password";


            Loggers.Default.getLogger().info( "Connecting to database..." );

            try ( Connection connection = DriverManager.getConnection( url, username, password ) )
            {
                Loggers.Default.getLogger().info( "Connected to database" );
                connection.createStatement().execute( sql );
            }

            return null;

        }, r -> new Thread( r ).start() );
    }
}
