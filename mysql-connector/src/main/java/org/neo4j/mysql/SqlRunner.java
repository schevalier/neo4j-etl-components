package org.neo4j.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.neo4j.io.AwaitHandle;
import org.neo4j.mysql.config.MySqlConnectionConfig;
import org.neo4j.utils.FutureUtils;
import org.neo4j.utils.Loggers;

public class SqlRunner
{
    private final MySqlConnectionConfig connectionConfig;
    private final String sql;

    public SqlRunner( MySqlConnectionConfig connectionConfig, String sql )
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
                            connectionConfig.uri(),
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
