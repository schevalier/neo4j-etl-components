package org.neo4j.integration.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.mysql.exportcsv.config.ConnectionConfig;
import org.neo4j.integration.util.FutureUtils;
import org.neo4j.integration.util.Loggers;

public class SqlRunner
{
    private final ConnectionConfig connectionConfig;
    private final String sql;

    public SqlRunner( ConnectionConfig connectionConfig, String sql )
    {
        this.connectionConfig = connectionConfig;
        this.sql = sql;
    }

    public AwaitHandle<Void> execute()
    {
        return new SqlRunnerAwaitHandle(
                FutureUtils.exceptionableFuture( () ->
                {
                    Loggers.MySql.log().fine( "Connecting to database..." );

                    try ( Connection connection = DriverManager.getConnection(
                            connectionConfig.uri().toString(),
                            connectionConfig.username(),
                            connectionConfig.password() ) )
                    {
                        Loggers.MySql.log().fine( "Connected to database" );
                        Loggers.MySql.log().finest( sql );

                        connection.createStatement().execute( sql );
                    }

                    return null;

                }, r -> new Thread( r ).start() ) );
    }

    private static class SqlRunnerAwaitHandle implements AwaitHandle<Void>
    {
        private final CompletableFuture<Void> future;

        private SqlRunnerAwaitHandle( CompletableFuture<Void> future )
        {
            this.future = future;
        }

        @Override
        public Void await() throws Exception
        {
            return future.get();
        }

        @Override
        public Void await( long timeout, TimeUnit unit ) throws Exception
        {
            return future.get( timeout, unit );
        }

        @Override
        public CompletableFuture<Void> toFuture()
        {
            return future;
        }
    }
}
