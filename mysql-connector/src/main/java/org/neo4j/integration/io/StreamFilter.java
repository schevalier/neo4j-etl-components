package org.neo4j.integration.io;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class StreamFilter<T> implements StreamEventHandler<T>
{
    private final StreamEventHandler<T> innerHandler;
    private final Predicate<String> filter;

    public StreamFilter( StreamEventHandler<T> innerHandler, Predicate<String> filter )
    {
        this.innerHandler = innerHandler;
        this.filter = filter;
    }

    @Override
    public void onLine( String line ) throws IOException
    {
        if ( filter.test( line ) )
        {
            innerHandler.onLine( line );
        }
    }

    @Override
    public void onException( Exception e )
    {
        innerHandler.onException( e );
    }

    @Override
    public void onCompleted() throws IOException
    {
        innerHandler.onCompleted();
    }

    @Override
    public T awaitContents( long timeout, TimeUnit unit ) throws Exception
    {
        return innerHandler.awaitContents( timeout, unit );
    }
}
