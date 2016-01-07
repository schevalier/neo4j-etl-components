package org.neo4j.ingest;

import org.neo4j.command_line.Commands;
import org.neo4j.command_line.Result;
import org.neo4j.ingest.config.ImportConfig;

public class Import
{
    private final ImportConfig config;

    public Import( ImportConfig config )
    {
        this.config = config;
    }

    public int execute() throws Exception
    {
        Commands.Builder.SetCommands builder = Commands.builder();

        config.addCommandsTo( builder );

        Commands commands = builder
                .inheritWorkingDirectory()
                .failOnNonZeroExitValue()
                .noTimeout()
                .inheritEnvironment()
                .build();

        Result result = commands.execute().await();

        return result.exitValue();
    }
}
