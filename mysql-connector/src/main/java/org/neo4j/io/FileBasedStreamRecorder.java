package org.neo4j.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Optional;

import static java.lang.String.format;

public class FileBasedStreamRecorder implements StreamRecorder
{
    public enum StreamType implements ProcessStreamType
    {
        StdOut
                {
                    @Override
                    public void configure( ProcessBuilder processBuilder, StreamRecorder streamRecorder )
                    {
                        processBuilder.redirectOutput(
                                ProcessBuilder.Redirect.to( toFileBasedStreamRecorder( streamRecorder ).file ) );
                    }

                    @Override
                    public StreamContents start( Process process, StreamRecorder streamRecorder )
                    {
                        FileBasedStreamRecorder fileBasedStreamRecorder = toFileBasedStreamRecorder( streamRecorder );
//                        new StreamSink(
//                                process.getInputStream(),
//                                fileBasedStreamRecorder.eventHandler() ).start();
                        return fileBasedStreamRecorder.contents();
                    }
                },
        StdErr
                {
                    @Override
                    public void configure( ProcessBuilder processBuilder, StreamRecorder streamRecorder )
                    {
                        processBuilder.redirectError(
                                ProcessBuilder.Redirect.to( toFileBasedStreamRecorder( streamRecorder ).file ) );
                    }

                    @Override
                    public StreamContents start( Process process, StreamRecorder streamRecorder )
                    {
                        FileBasedStreamRecorder fileBasedStreamRecorder = toFileBasedStreamRecorder( streamRecorder );
//                        new StreamSink(
//                                process.getErrorStream(),
//                                fileBasedStreamRecorder.eventHandler() ).start();
                        return fileBasedStreamRecorder.contents();
                    }
                };

        FileBasedStreamRecorder toFileBasedStreamRecorder( StreamRecorder streamRecorder )
        {
            if ( !FileBasedStreamRecorder.class.isAssignableFrom( streamRecorder.getClass() ) )
            {
                throw new IllegalArgumentException( format( "streamRecorder is not an instance of %s ",
                        FileBasedStreamRecorder.class.getSimpleName() ) );
            }

            return ((FileBasedStreamRecorder) streamRecorder);
        }
    }

    private final File file;
    private volatile IOException ex;

    public FileBasedStreamRecorder( File file )
    {
        this.file = file;
    }

    @Override
    public StreamContents start( InputStream input )
    {
        new StreamSink( input, eventHandler() ).start();
        return contents();
    }

    private StreamContents contents()
    {
        FileContentsSummary contents = new FileContentsSummary( file );

        return new StreamContents()
        {
            @Override
            public Optional<File> file() throws IOException
            {
                if ( ex != null )
                {
                    throw ex;
                }
                return contents.file();
            }

            @Override
            public String value() throws IOException
            {
                if ( ex != null )
                {
                    throw ex;
                }
                return contents.value();
            }
        };
    }

    private StreamEventHandler eventHandler()
    {
        return new StreamEventHandler()
        {
            BufferedWriter writer;

            @Override
            public void onLine( String line ) throws IOException
            {
                if ( writer == null )
                {
                    //noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                    writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( file, true ) ));
                }

                writer.write( line );
                writer.newLine();
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
            public void onCompleted() throws IOException
            {
                if ( writer != null )
                {
                    writer.flush();
                    writer.close();
                }
            }
        };
    }
}

