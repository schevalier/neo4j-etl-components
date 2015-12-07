package org.neo4j.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class InMemoryStreamRecorder implements StreamEventHandler<String>
{
    private final StringBuilder stringBuilder = new StringBuilder();
    private final StreamContentsHandle<String> streamContentsHandle;

    public InMemoryStreamRecorder()
    {
        streamContentsHandle = new StreamContentsHandle<>( new Supplier<String>()
        {
            @Override
            public String get()
            {
                return stringBuilder.toString();
            }
        } );
    }

    @Override
    public void onLine( String line )
    {
        stringBuilder.append( line ).append( System.lineSeparator() );
    }

    @Override
    public void onException( IOException e )
    {
        streamContentsHandle.addException( e );
    }

    @Override
    public void onCompleted()
    {
        streamContentsHandle.ready();
    }

    @Override
    public String awaitContents( long timeout, TimeUnit unit ) throws IOException
    {
        return streamContentsHandle.await( timeout, unit );
    }
}

