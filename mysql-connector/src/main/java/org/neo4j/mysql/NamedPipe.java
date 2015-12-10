package org.neo4j.mysql;

import java.io.OutputStreamWriter;
import java.io.Writer;

import org.neo4j.command_line.Commands;

public class NamedPipe
{
    private static final int DEFAULT_BUFFER_SIZE = 1000;

    private final String name;
    private final PipeReader reader;

    public NamedPipe( String name, PipeReader reader )
    {
        this.name = name;
        this.reader = reader;
    }

    public Writer open() throws Exception
    {
        Commands.commands( "mkfifo", name ).execute().await();
        Commands.commands( "chmod", "666", name ).execute().await();

        reader.open();

        try ( StreamOpener fifo = new StreamOpener( name, DEFAULT_BUFFER_SIZE, reader ) )
        {
            return new OutputStreamWriter( fifo.open() );
        }
    }
}
