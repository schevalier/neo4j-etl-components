package org.neo4j.io;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class StreamEventLatch implements StreamEventHandler<Boolean>
{
    private final StreamContentsHandle<Boolean> streamContentsHandle = new StreamContentsHandle<>(
            new Supplier<Boolean>()
            {
                @Override
                public Boolean get()
                {
                    return value.get();
                }
            } );

    private AtomicBoolean value = new AtomicBoolean( false );

    @Override
    public void onLine( String line ) throws IOException
    {
        value.set( true );
        onCompleted();
    }

    @Override
    public void onException( Exception e )
    {
        streamContentsHandle.addException( e );
    }

    @Override
    public void onCompleted() throws IOException
    {
        streamContentsHandle.ready();
    }

    @Override
    public Boolean awaitContents( long timeout, TimeUnit unit ) throws Exception
    {
        return streamContentsHandle.await( timeout, unit );
    }
}
