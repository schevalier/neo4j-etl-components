package org.neo4j.etl.neo4j.importcsv;

import org.neo4j.etl.neo4j.importcsv.config.ImportConfig;
import org.neo4j.etl.process.Commands;
import org.neo4j.etl.process.Result;

public class ImportFromCsvCommand
{
    private final ImportConfig config;

    public ImportFromCsvCommand( ImportConfig config )
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
