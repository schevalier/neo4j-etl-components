package org.neo4j.integration.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.mysql.metadata.ConnectionConfig;
import org.neo4j.integration.util.FutureUtils;
import org.neo4j.integration.util.Loggers;

public class SqlRunner implements AutoCloseable
{
    private final Connection connection;

    public SqlRunner( ConnectionConfig connectionConfig ) throws SQLException
    {
        Loggers.MySql.log().fine( "Connecting to database..." );

        connection = DriverManager.getConnection(
                connectionConfig.uri().toString(),
                connectionConfig.username(),
                connectionConfig.password() );

        Loggers.MySql.log().fine( "Connected to database" );
    }

    public AwaitHandle<ResultSet> execute( String sql )
    {

        return new SqlRunnerAwaitHandle(
                FutureUtils.exceptionableFuture( () ->
                {
                    Loggers.MySql.log().finest( sql );
                    return connection.createStatement().executeQuery( sql );

                }, r -> new Thread( r ).start() ) );
    }

    @Override
    public void close() throws Exception
    {
        connection.close();
    }

    private static class SqlRunnerAwaitHandle implements AwaitHandle<ResultSet>
    {
        private final CompletableFuture<ResultSet> future;

        private SqlRunnerAwaitHandle( CompletableFuture<ResultSet> future )
        {
            this.future = future;
        }

        @Override
        public ResultSet await() throws Exception
        {
            return future.get();
        }

        @Override
        public ResultSet await( long timeout, TimeUnit unit ) throws Exception
        {
            return future.get( timeout, unit );
        }

        @Override
        public CompletableFuture<ResultSet> toFuture()
        {
            return future;
        }
    }
}
