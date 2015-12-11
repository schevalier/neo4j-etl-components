package org.neo4j.mysql;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.neo4j.command_line.Commands;
import org.neo4j.utils.OperatingSystem;

public class NamedPipe implements AutoCloseable
{
    private static final int DEFAULT_BUFFER_SIZE = 1000;

    private final String name;
    private final PipeReader reader;

    public NamedPipe( String name, PipeReader reader )
    {
        this.name = name;
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
        Commands.commands( "mkfifo", name ).execute().await();
        //Commands.commands( "chmod", "666", name ).execute().await();
    }

    private void openReaderAsync()
    {
        new Thread( reader::open ).start();
    }

    private OutputStream createStream() throws Exception
    {
        return new AsyncFileOpener( new File( name ), DEFAULT_BUFFER_SIZE, reader ).open();
    }

    @Override
    public void close() throws Exception
    {
        Files.deleteIfExists( Paths.get( name ) );
    }
}
