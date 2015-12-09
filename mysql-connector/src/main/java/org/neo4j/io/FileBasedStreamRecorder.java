package org.neo4j.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

public class FileBasedStreamRecorder implements StreamEventHandler<FileDigest>
{
    private final BufferedWriter writer;
    private final StreamContentsHandle<FileDigest> streamContentsHandle;

    public FileBasedStreamRecorder( File file ) throws IOException
    {
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();

        this.writer = new BufferedWriter( new FileWriter( file, true ) );
        this.streamContentsHandle = new StreamContentsHandle<>( () -> new FileDigest( file ) );
    }

    @Override
    public void onLine( String line ) throws IOException
    {
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
        try ( Writer w = writer )
        {
            w.flush();
        }
        finally
        {
            streamContentsHandle.ready();
        }
    }

    @Override
    public FileDigest awaitContents( long timeout, TimeUnit unit ) throws IOException
    {
        return streamContentsHandle.await( timeout, unit );
    }
}

