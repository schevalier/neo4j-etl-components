package org.neo4j.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

public class InMemoryStreamRecorder implements StreamRecorder<String>
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
    public StreamContentsHandle<String> start( InputStream input )
    {
        new StreamSink( input, new EventHandler() ).start();

        return streamContentsHandle;
    }

    private class EventHandler implements StreamEventHandler
    {
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
    }
}

