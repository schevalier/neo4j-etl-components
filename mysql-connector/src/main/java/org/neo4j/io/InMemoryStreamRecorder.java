package org.neo4j.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

public class InMemoryStreamRecorder implements StreamRecorder
{
    public enum StreamType implements ProcessStreamType
    {
        StdOut
                {
                    @Override
                    public void configure( ProcessBuilder processBuilder, StreamRecorder streamRecorder )
                    {
                        processBuilder.redirectOutput( ProcessBuilder.Redirect.PIPE );
                    }

                    @Override
                    public StreamContents start( Process process, StreamRecorder streamRecorder )
                    {
                        InMemoryStreamRecorder inMemoryStreamRecorder = toInMemoryStreamRecorder( streamRecorder );
                        new StreamSink(
                                process.getInputStream(),
                                inMemoryStreamRecorder.eventHandler() ).start();
                        return inMemoryStreamRecorder.contents();
                    }
                },
        StdErr
                {
                    @Override
                    public void configure( ProcessBuilder processBuilder, StreamRecorder streamRecorder )
                    {
                        processBuilder.redirectError( ProcessBuilder.Redirect.PIPE );
                    }

                    @Override
                    public StreamContents start( Process process, StreamRecorder streamRecorder )
                    {
                        InMemoryStreamRecorder inMemoryStreamRecorder = toInMemoryStreamRecorder( streamRecorder );
                        new StreamSink(
                                process.getErrorStream(),
                                inMemoryStreamRecorder.eventHandler() ).start();
                        return inMemoryStreamRecorder.contents();
                    }
                };

        InMemoryStreamRecorder toInMemoryStreamRecorder( StreamRecorder streamRecorder )
        {
            if ( !InMemoryStreamRecorder.class.isAssignableFrom( streamRecorder.getClass() ) )
            {
                throw new IllegalArgumentException( format( "streamRecorder is not an instance of %s ",
                        InMemoryStreamRecorder.class.getSimpleName() ) );
            }

            return ((InMemoryStreamRecorder) streamRecorder);
        }
    }

    private final StringBuilder stringBuilder = new StringBuilder();
    private final CountDownLatch latch = new CountDownLatch( 1 );
    private volatile IOException ex;

    @Override
    public StreamContents start( InputStream input )
    {
        new StreamSink( input, eventHandler() ).start();
        return contents();
    }

    private StreamContents contents()
    {
        try
        {
            latch.await( 5, TimeUnit.SECONDS );
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
        }

        return new StreamContents()
        {

            @Override
            public Optional<File> file() throws IOException
            {
                if ( ex != null )
                {
                    throw ex;
                }
                return Optional.empty();
            }

            @Override
            public String value() throws IOException
            {
                if ( ex != null )
                {
                    throw ex;
                }
                return stringBuilder.toString();
            }
        };
    }

    private StreamEventHandler eventHandler()
    {
        return new StreamEventHandler()
        {
            @Override
            public void onLine( String line )
            {
                stringBuilder.append( line ).append( System.lineSeparator() );
            }

            @Override
            public void onException( IOException e )
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

            @Override
            public void onCompleted()
            {
                latch.countDown();
            }
        };
    }
}

