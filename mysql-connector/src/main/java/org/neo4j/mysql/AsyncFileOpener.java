package org.neo4j.mysql;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

class AsyncFileOpener extends Thread implements Opener<OutputStream>
{
    private volatile BufferedOutputStream output = null;
    private volatile Exception ex;
    private final File file;
    private final int size;
    private final Exceptions exceptions;

    AsyncFileOpener( File file, int size, Exceptions exceptions )
    {
        this.file = file;
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

//            try
//            {
            exceptions.rethrow();
//            }
//            catch ( Exception e )
//            {
            //noinspection EmptyTryBlock
//                try ( AutoCloseable tryClose = new BufferedInputStream( new FileInputStream( file ) ) )
//                {
//                }
//                finally
//                {
//                    join();
//                }
//                throw e;
//            }
            if ( ex != null )
            {
                throw ex;
            }
        }


        return output;
    }

    public void run()
    {
        try
        {
            output = new BufferedOutputStream( new FileOutputStream( file ), size );
        }
        catch ( Exception ex )
        {
            this.ex = ex;
        }
    }
}
