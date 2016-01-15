package org.neo4j.integration.neo4j.importcsv;

import org.neo4j.integration.process.Commands;
import org.neo4j.integration.process.Result;
import org.neo4j.integration.neo4j.importcsv.config.ImportConfig;

public class ImportCommand
{
    private final ImportConfig config;

    public ImportCommand( ImportConfig config )
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
