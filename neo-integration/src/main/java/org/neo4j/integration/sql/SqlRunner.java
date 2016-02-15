package org.neo4j.integration.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.sql.metadata.ConnectionConfig;
import org.neo4j.integration.util.FutureUtils;
import org.neo4j.integration.util.Loggers;

public class SqlRunner implements AutoCloseable
{
    private final Connection connection;

    public SqlRunner( ConnectionConfig connectionConfig ) throws SQLException, ClassNotFoundException
    {
        Loggers.MySql.log().fine( "Connecting to database..." );

        Class.forName( connectionConfig.driverClassName() );

        connection = DriverManager.getConnection(
                connectionConfig.uri().toString(),
                connectionConfig.username(),
                connectionConfig.password() );

        Loggers.MySql.log().fine( "Connected to database" );
    }

    public AwaitHandle<Results> execute( String sql )
    {
        return new SqlRunnerAwaitHandle(
                FutureUtils.<Results>exceptionableFuture( () ->
                {
                    Loggers.MySql.log().finest( sql );
                    return new SqlResults( connection.createStatement().executeQuery( sql ) );

                }, r -> new Thread( r ).start() ) );
    }

    @Override
    public void close() throws Exception
    {
        connection.close();
    }

    private static class SqlRunnerAwaitHandle implements AwaitHandle<Results>
    {
        private final CompletableFuture<Results> future;

        private SqlRunnerAwaitHandle( CompletableFuture<Results> future )
        {
            this.future = future;
        }

        @Override
        public Results await() throws Exception
        {
            return future.get();
        }

        @Override
        public Results await( long timeout, TimeUnit unit ) throws Exception
        {
            return future.get( timeout, unit );
        }

        @Override
        public CompletableFuture<Results> toFuture()
        {
            return future;
        }
    }

    private static class SqlResults implements Results
    {
        private final ResultSet results;

        public SqlResults( ResultSet results )
        {
            this.results = results;
        }

        @Override
        public boolean next() throws Exception
        {
            return results.next();
        }

        @Override
        public String getString( String columnLabel ) throws Exception
        {
            return results.getString( columnLabel );
        }

        @Override
        public void close() throws Exception
        {
            results.close();
        }
    }
}
