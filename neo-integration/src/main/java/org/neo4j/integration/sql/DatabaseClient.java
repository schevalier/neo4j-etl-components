package org.neo4j.integration.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.util.FutureUtils;
import org.neo4j.integration.util.Loggers;

import static java.lang.String.format;

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
        public Stream<Map<String, String>> stream()
        {
            Collection<String> columnLabels = new ArrayList<>();

            try
            {
                ResultSetMetaData metaData = results.getMetaData();
                int columnCount = metaData.getColumnCount();
                for ( int i = 1; i <= columnCount; i++ )
                {
                    columnLabels.add( metaData.getColumnLabel( i ) );
                }
            }
            catch ( SQLException e )
            {
                throw new IllegalStateException( "Error while getting column labels from SQL result set", e );
            }

            return StreamSupport.stream( new ResultSetSpliterator( results, columnLabels ), false );
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

        private static class ResultSetSpliterator implements Spliterator<Map<String, String>>
        {
            private final ResultSet results;
            private final Collection<String> columnLabels;

            public ResultSetSpliterator( ResultSet results, Collection<String> columnLabels )
            {
                this.results = results;
                this.columnLabels = columnLabels;
            }

            @Override
            public boolean tryAdvance( Consumer<? super Map<String, String>> action )
            {
                boolean hasNext;
                try
                {
                    hasNext = results.next();
                }
                catch ( SQLException e )
                {
                    throw new IllegalStateException( "Error while iterating SQL result set", e );
                }
                if ( hasNext )
                {
                    Map<String, String> map = new HashMap<>();
                    for ( String columnLabel : columnLabels )
                    {
                        try
                        {
                            map.put( columnLabel, results.getString( columnLabel ) );
                        }
                        catch ( SQLException e )
                        {
                            throw new IllegalStateException(
                                    format( "Error while accessing '%s' in SQL result set", columnLabel ), e );
                        }

                    }
                    action.accept( map );
                    return true;
                }
                else
                {
                    return false;
                }
            }

            @Override
            public Spliterator<Map<String, String>> trySplit()
            {
                return null;
            }

            @Override
            public long estimateSize()
            {
                return 0;
            }

            @Override
            public int characteristics()
            {
                return 0;
            }
        }
    }
}
