package org.neo4j.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Optional;

public class FileBasedStreamRecorder implements StreamRecorder
{
    private final File file;
    private final DeferredStreamContents streamContents;

    public FileBasedStreamRecorder( File file )
    {
        this.file = file;
        this.streamContents = new DeferredStreamContents()
        {
            FileContentsSummary contents = new FileContentsSummary( file );

            @Override
            Optional<File> getFile() throws IOException
            {
                return contents.file();
            }

            @Override
            String getValue() throws IOException
            {
                return contents.value();
            }
        };
    }

    @Override
    public StreamContents start( InputStream input )
    {
        new StreamSink( input, new EventHandler() ).start();

        return streamContents;
    }

    private class EventHandler implements StreamEventHandler
    {
        BufferedWriter writer;

        @Override
        public void onLine( String line ) throws IOException
        {
            if ( writer == null )
            {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
                writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( file, true ) ) );
            }

            writer.write( line );
            writer.newLine();
        }

        @Override
        public void onException( IOException e )
        {
            streamContents.addException( e );
        }

        @Override
        public void onCompleted() throws IOException
        {
            try
            {
                if ( writer != null )
                {
                    writer.flush();
                    writer.close();
                }
            }
            finally
            {
                streamContents.ready();
            }
        }
    }
}

