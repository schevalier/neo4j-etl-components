package org.neo4j.integration.commands;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.neo4j.integration.neo4j.importcsv.config.ImportConfig;
import org.neo4j.integration.util.ResourceRule;
import org.neo4j.integration.util.TemporaryDirectory;

import static org.hamcrest.CoreMatchers.startsWith;

public class EnvironmentTest
{
    @Rule
    public final ResourceRule<Path> tempDirectory = new ResourceRule<>( TemporaryDirectory.temporaryDirectory() );

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test()
    public void shouldThrowExceptionIfImportToolCannotBeFound() throws Exception
    {
        // given
        thrown.expect( IllegalArgumentException.class );

        //when
        Environment environment = new Environment(
                tempDirectory.get(),
                tempDirectory.get(),
                tempDirectory.get(),
                false );

        environment.prepare();

        // then
        thrown.expectMessage( startsWith( "Unable to find import tool" ) );
    }

    @Test
    public void shouldThrowExceptionIfDestinationAlreadyExistsAndForceHasNotBeenSpecified() throws Exception
    {
        thrown.expect( IllegalStateException.class );

        // given
        Path destinationDirectory = tempDirectory.get().resolve( "graph.db" );
        Files.createDirectories( destinationDirectory );

        Path importToolDirectory = tempDirectory.get().resolve( "neo4j/bin" );
        Files.createDirectories( importToolDirectory );

        Path importTool = importToolDirectory.resolve( ImportConfig.IMPORT_TOOL );
        Files.createFile( importTool );

        // when
        Environment environment = new Environment(
                importToolDirectory,
                destinationDirectory,
                tempDirectory.get(),
                false );

        environment.prepare();

        // then
        thrown.expectMessage( startsWith( "Destination already exists" ) );
    }

    @Test
    public void shouldNotThrowExceptionIfDestinationAlreadyExistsAndForceHasBeenSpecified() throws Exception
    {
        // given
        Path destinationDirectory = tempDirectory.get().resolve( "graph.db" );
        Files.createDirectories( destinationDirectory );

        Path importToolDirectory = tempDirectory.get().resolve( "neo4j/bin" );
        Files.createDirectories( importToolDirectory );

        Path importTool = importToolDirectory.resolve( ImportConfig.IMPORT_TOOL );
        Files.createFile( importTool );

        // when
        Environment environment = new Environment( importToolDirectory, destinationDirectory, tempDirectory.get(),
                true );
        environment.prepare();

    }
}
