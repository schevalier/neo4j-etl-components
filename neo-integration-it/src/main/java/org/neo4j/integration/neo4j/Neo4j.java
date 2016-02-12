package org.neo4j.integration.neo4j;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.neo4j.integration.process.Commands;
import org.neo4j.integration.process.Result;

public class Neo4j implements AutoCloseable
{
    private final Path directory;

    public Neo4j( Path directory )
    {
        this.directory = directory;
    }

    public Path binDirectory()
    {
        return directory.resolve( "bin" );
    }

    public void start() throws Exception
    {
        Result.Evaluator resultEvaluator = r ->
                (r.exitValue() == 0) ||
                (r.exitValue() == 1 && r.stdout().startsWith( "Service is already running" ));

        Commands.builder( "bin/neo4j", "start" )
                .workingDirectory( directory )
                .commandResultEvaluator( resultEvaluator )
                .timeout( 10, TimeUnit.SECONDS )
                .inheritEnvironment()
                .build()
                .execute()
                .await();
    }

    public void stop() throws Exception
    {
        Commands.builder( "bin/neo4j", "stop" )
                .workingDirectory( directory )
                .failOnNonZeroExitValue()
                .timeout( 10, TimeUnit.SECONDS )
                .inheritEnvironment()
                .build()
                .execute()
                .await();
    }

    @Override
    public void close() throws Exception
    {
        stop();
    }
}
