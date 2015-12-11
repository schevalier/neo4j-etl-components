package org.neo4j.mysql;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;

import org.neo4j.command_line.Commands;
import org.neo4j.utils.OperatingSystem;

public class NamedPipe implements AutoCloseable
{
    private static final int DEFAULT_BUFFER_SIZE = 1000;

    private final File file;
    private final PipeReader reader;

    public NamedPipe( String name, PipeReader reader )
    {
        this.file = new File( name );
        this.reader = reader;
    }

    public OutputStream open() throws Exception
    {
        if ( OperatingSystem.isWindows() )
        {
            throw new IllegalStateException( "Named pipes not supported on Windows" );
        }

        createFifo();
        openReaderAsync();

        return createStream();
    }

    private void createFifo() throws Exception
    {
        Commands.commands( "mkfifo", file.getAbsolutePath() ).execute().await();
        //Commands.commands( "chmod", "666", name ).execute().await();
    }

    private void openReaderAsync()
    {
        new Thread( reader::open ).start();
    }

    private OutputStream createStream() throws Exception
    {
        return new AsyncFileOpener( file, DEFAULT_BUFFER_SIZE, reader ).open();
    }

    @Override
    public void close() throws Exception
    {
        Files.deleteIfExists( file.toPath() );
    }
}
