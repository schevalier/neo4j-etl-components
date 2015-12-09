package org.neo4j.io;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class StreamContentsHandle<T>
{
    private final CountDownLatch latch = new CountDownLatch( 1 );
    private final Supplier<T> supplier;
    private volatile Exception ex;

    public StreamContentsHandle( Supplier<T> supplier )
    {
        this.supplier = supplier;
    }

    public T await( long timeout, TimeUnit unit ) throws Exception
    {
        try
        {
            latch.await( timeout, unit );
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
        }

        if ( ex != null )
        {
            throw ex;
        }

        return supplier.get();
    }

    void ready()
    {
        latch.countDown();
    }

    void addException( Exception e )
    {
        if ( ex == null )
        {
            ex = e;
        }
        else
        {
            ex.addSuppressed( e );
        }
    }
}
