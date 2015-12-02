package org.neo4j.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class InMemoryStreamRecorder implements StreamRecorder
{
    private final StringBuilder stringBuilder = new StringBuilder();
    private final DeferredStreamContents streamContents;

    public InMemoryStreamRecorder()
    {
        streamContents = new DeferredStreamContents()
        {
            @Override
            Optional<File> getFile() throws IOException
            {
                return Optional.empty();
            }

            @Override
            String getValue() throws IOException
            {
                return stringBuilder.toString();
            }
        };
    }

    @Override
    public DeferredStreamContents start( InputStream input )
    {
        new StreamSink( input, new EventHandler() ).start();

        return streamContents;
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
            streamContents.addException( e );
        }

        @Override
        public void onCompleted()
        {
            streamContents.ready();
        }
    }
}

