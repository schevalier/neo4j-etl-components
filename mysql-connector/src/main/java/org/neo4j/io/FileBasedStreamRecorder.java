package org.neo4j.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class FileBasedStreamRecorder implements StreamRecorder<FileDigest>
{
    private final File file;
    private final StreamContentsHandle<FileDigest> streamContentsHandle;

    public FileBasedStreamRecorder( File file )
    {
        this.file = file;
        this.streamContentsHandle = new StreamContentsHandle<>( () -> new FileDigest( file ) );
    }

    @Override
    public StreamContentsHandle<FileDigest> start( InputStream input )
    {
        new StreamSink( input, new EventHandler() ).start();

        return streamContentsHandle;
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
            streamContentsHandle.addException( e );
        }

        @Override
        public void onCompleted() throws IOException
        {
            if ( writer == null )
            {
                streamContentsHandle.ready();
                return;
            }

            try ( Writer w = writer )
            {
                w.flush();
            }
            finally
            {
                streamContentsHandle.ready();
            }
        }
    }
}

