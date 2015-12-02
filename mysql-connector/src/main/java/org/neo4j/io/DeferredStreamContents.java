package org.neo4j.io;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class DeferredStreamContents implements StreamContents
{
    private final CountDownLatch latch = new CountDownLatch( 1 );
    private volatile IOException ex;

    abstract Optional<File> getFile() throws IOException;

    abstract String getValue() throws IOException;

    @Override
    public Optional<File> file() throws IOException
    {
        try
        {
            latch.await( 5, TimeUnit.SECONDS );
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
        }

        if ( ex != null )
        {
            throw ex;
        }

        return getFile();
    }

    @Override
    public String value() throws IOException
    {
        try
        {
            latch.await( 5, TimeUnit.SECONDS );
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
        }

        if ( ex != null )
        {
            throw ex;
        }

        return getValue();
    }

    void ready()
    {
        latch.countDown();
    }

    void addException( IOException e )
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
