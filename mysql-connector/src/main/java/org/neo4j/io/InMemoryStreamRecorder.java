package org.neo4j.io;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static java.lang.String.format;

public class InMemoryStreamRecorder implements StreamEventHandler<String>
{
    private static final int DEFAULT_MAX_NUMBER_OF_LINES = 100;

    private final int maxNumberOfLines;
    private final StringBuilder stringBuilder = new StringBuilder();
    private final StreamContentsHandle<String> streamContentsHandle;

    private int numberOfLines;
    private String lastLine;


    public InMemoryStreamRecorder()
    {
        this( DEFAULT_MAX_NUMBER_OF_LINES );
    }

    public InMemoryStreamRecorder( int maxNumberOfLines )
    {
        this.maxNumberOfLines = maxNumberOfLines;
        this.streamContentsHandle = new StreamContentsHandle<>( new Supplier<String>()
        {
            @Override
            public String get()
            {
                return numberOfLines > maxNumberOfLines ?
                        format( "%s%n[...]%n%s", stringBuilder, lastLine ) :
                        stringBuilder.toString();
            }
        } );
    }

    @Override
    public void onLine( String line )
    {
        if ( ++numberOfLines <= maxNumberOfLines )
        {
            if (numberOfLines > 1)
            {
                stringBuilder.append( System.lineSeparator() );
            }
            stringBuilder.append( line );
        }
        else
        {
            lastLine = line;
        }
    }

    @Override
    public void onException( Exception e )
    {
        streamContentsHandle.addException( e );
    }

    @Override
    public void onCompleted()
    {
        streamContentsHandle.ready();
    }

    @Override
    public String awaitContents( long timeout, TimeUnit unit ) throws Exception
    {
        return streamContentsHandle.await( timeout, unit );
    }
}

