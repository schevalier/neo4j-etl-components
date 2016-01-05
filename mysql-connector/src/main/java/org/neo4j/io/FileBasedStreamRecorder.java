package org.neo4j.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

public class FileBasedStreamRecorder implements StreamEventHandler<FileDigest>
{
    private final BufferedWriter writer;
    private final StreamContentsHandle<FileDigest> streamContentsHandle;

    public FileBasedStreamRecorder( Path file ) throws IOException
    {
        this.writer = Files.newBufferedWriter( file, StandardOpenOption.APPEND );
        this.streamContentsHandle = new StreamContentsHandle<>( () -> new FileDigest( file ) );
    }

    @Override
    public void onLine( String line ) throws IOException
    {
        writer.write( line );
        writer.newLine();
    }

    @Override
    public void onException( Exception e )
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
    public FileDigest awaitContents( long timeout, TimeUnit unit ) throws Exception
    {
        return streamContentsHandle.await( timeout, unit );
    }
}

