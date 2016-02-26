package org.neo4j.integration.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.neo4j.integration.util.Loggers;

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

            boolean allowContinue = true;
            String line;

            while ( allowContinue )
            {
                try
                {
                    line = bufferedReader.readLine();
                    if ( line != null )
                    {
                        eventHandler.onLine( line );
                    }
                    else
                    {
                        allowContinue = false;
                    }
                }
                catch ( IOException e )
                {
                    if ( e.getMessage().equals( "Interrupted system call" ) )
                    {
                        Loggers.Default.log( Level.WARNING, e.getMessage() );
                    }
                    else
                    {
                        throw e;
                    }
                }
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

