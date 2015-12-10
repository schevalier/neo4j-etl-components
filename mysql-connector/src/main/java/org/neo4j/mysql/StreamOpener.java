package org.neo4j.mysql;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

class StreamOpener extends Thread implements Opener<OutputStream>, AutoCloseable
{
    private volatile BufferedOutputStream output = null;
    private volatile Exception ex;
    private final String name;
    private final int size;
    private final Exceptions exceptions;

    StreamOpener( String name, int size, Exceptions exceptions )
    {
        this.name = name;
        this.size = size;
        this.exceptions = exceptions;
    }

    @Override
    public OutputStream open() throws Exception
    {
        start();

        while ( getState() != State.TERMINATED )
        {
            join( TimeUnit.MILLISECONDS.toMillis( 100 ) );

            try
            {
                exceptions.rethrow();
            }
            catch ( Exception e )
            {
                //noinspection EmptyTryBlock
                try ( AutoCloseable tryClose = new BufferedInputStream( new FileInputStream( name ) ) )
                {
                }
                finally
                {
                    join();
                }
                throw e;
            }
        }

        return output;
    }

    public void run()
    {
        try
        {
            output = new BufferedOutputStream( new FileOutputStream( name ), size );
        }
        catch ( Exception ex )
        {
            this.ex = ex;
        }
    }

    @Override
    public void close() throws Exception
    {
        if ( ex != null )
        {
            throw ex;
        }
    }
}
