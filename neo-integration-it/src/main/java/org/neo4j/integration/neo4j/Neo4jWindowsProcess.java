package org.neo4j.integration.neo4j;

import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.neo4j.integration.process.Commands;
import org.neo4j.integration.util.Loggers;

class Neo4jWindowsProcess
{
    private final Neo4j neo4j;

    Neo4jWindowsProcess( Neo4j neo4j )
    {
        this.neo4j = neo4j;
    }

    void kill() throws Exception
    {
        if ( Files.notExists( neo4j.debugLog() ) )
        {
            throw new IllegalStateException( "Neo4j debug log does not exist: " + neo4j.debugLog() );
        }

        Optional<String> processIdLine;
        try ( Stream<String> lines = Files.lines( neo4j.debugLog() ) )
        {
            processIdLine = lines.filter( l -> l.contains( "Process id:" ) ).findFirst();
        }

        if ( processIdLine.isPresent() )
        {
            String[] elements = processIdLine.get().split( " " );
            String processId = elements[elements.length - 1].split( "@" )[0];

            Loggers.Default.log( Level.INFO, "Neo4j process ID: {0}", processId );

            Commands.builder( "taskkill", "/F", "/t", "/pid", processId )
                    .inheritWorkingDirectory()
                    .failOnNonZeroExitValue()
                    .timeout( 5, TimeUnit.MINUTES )
                    .inheritEnvironment()
                    .build()
                    .execute()
                    .await();
        }
        else
        {
            throw new IllegalStateException( "Unable to determine Neo4j process ID" );
        }
    }
}
