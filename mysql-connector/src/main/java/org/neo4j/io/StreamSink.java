package org.neo4j.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamSink extends Thread
{
    private final InputStream input;
    private final StreamEventHandler eventHandler;

    public StreamSink( InputStream input, StreamEventHandler eventHandler )
    {
        this.input = input;
        this.eventHandler = eventHandler;
    }

    public void run()
    {
        try
        {
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( input ) );

            String line;
            while ( (line = bufferedReader.readLine()) != null )
            {
                eventHandler.onLine( line );
            }

            eventHandler.onCompleted();
        }
        catch ( IOException e )
        {
            try
            {
                if ( !e.getMessage().equals( "Stream closed" ) )
                {
                    eventHandler.onException( e );
                }
            }
            finally
            {
                try
                {
                    eventHandler.onCompleted();
                }
                catch ( IOException ex )
                {
                    eventHandler.onException( ex );
                }
            }
        }
    }
}

