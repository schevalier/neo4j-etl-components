package org.neo4j.integration.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.util.FutureUtils;
import org.neo4j.integration.util.Loggers;

public class DatabaseClient implements AutoCloseable
{
    private final Connection connection;

    public DatabaseClient( ConnectionConfig connectionConfig ) throws SQLException, ClassNotFoundException
    {
        Loggers.Sql.log().fine( "Connecting to database..." );

        Class.forName( connectionConfig.driverClassName() );

        connection = DriverManager.getConnection(
                connectionConfig.uri().toString(),
                connectionConfig.username(),
                connectionConfig.password() );

        Loggers.Sql.log().fine( "Connected to database" );
    }

    public AwaitHandle<QueryResults> executeQuery( String sql )
    {
        return new DatabaseClientAwaitHandle<>(
                FutureUtils.<QueryResults>exceptionableFuture( () ->
                {
                    Loggers.Sql.log().finest( sql );
                    return new SqlQueryResults( connection.createStatement().executeQuery( sql ) );

                }, r -> new Thread( r ).start() ) );
    }

    public AwaitHandle<Boolean> execute( String sql )
    {
        return new DatabaseClientAwaitHandle<>(
                FutureUtils.exceptionableFuture( () ->
                {
                    Loggers.Sql.log().finest( sql );
                    return connection.createStatement().execute( sql );

                }, r -> new Thread( r ).start() ) );
    }

    @Override
    public void close() throws Exception
    {
        connection.close();
    }

    private static class DatabaseClientAwaitHandle<T> implements AwaitHandle<T>
    {
        private final CompletableFuture<T> future;

        private DatabaseClientAwaitHandle( CompletableFuture<T> future )
        {
            this.future = future;
        }

        @Override
        public T await() throws Exception
        {
            return future.get();
        }

        @Override
        public T await( long timeout, TimeUnit unit ) throws Exception
        {
            return future.get( timeout, unit );
        }

        @Override
        public CompletableFuture<T> toFuture()
        {
            return future;
        }
    }

    private static class SqlQueryResults implements QueryResults
    {
        private final ResultSet results;

        public SqlQueryResults( ResultSet results )
        {
            this.results = results;
        }

        @Override
        public boolean next() throws Exception
        {
            return results.next();
        }

        @Override
        public Stream<Map<String, String>> streamOfResults()
        {
            try
            {
                Collection<String> columnLabels = new ArrayList<>();
                ResultSetMetaData metaData = results.getMetaData();
                int columnCount = metaData.getColumnCount();
                for ( int i = 1; i <= columnCount; i++ )
                {
                    columnLabels.add( metaData.getColumnLabel( i ) );
                }

                List<Map<String, String>> listOfResults = new ArrayList<>();
                while ( results.next() )
                {
                    Map<String, String> map = new HashMap<>();
                    for ( String columnLabel : columnLabels )
                    {
                        map.put( columnLabel, results.getString( columnLabel ) );

                    }
                    listOfResults.add( map );
                }
                return listOfResults.stream();
            }
            catch ( SQLException e )
            {
                throw new IllegalStateException( e );
            }
        }

        @Override
        public String getString( String columnLabel )
        {
            try
            {
                return results.getString( columnLabel );
            }
            catch ( SQLException e )
            {
                throw new RuntimeException( e );
            }
        }

        @Override
        public void close() throws Exception
        {
            results.close();
        }
    }
}
