package org.neo4j.integration.environment;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.neo4j.integration.neo4j.importcsv.config.ImportConfig;
import org.neo4j.integration.util.ResourceRule;
import org.neo4j.integration.util.TemporaryDirectory;

import static org.hamcrest.CoreMatchers.startsWith;

public class DestinationDirectorySupplierTest
{
    @Rule
    public final ResourceRule<Path> tempDirectory = new ResourceRule<>( TemporaryDirectory.temporaryDirectory() );

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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

        DestinationDirectorySupplier supplier = new DestinationDirectorySupplier( destinationDirectory, false );

        // when
        supplier.supply();

        // then
        thrown.expectMessage( startsWith( "Destination directory already exists" ) );
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
        DestinationDirectorySupplier supplier = new DestinationDirectorySupplier( destinationDirectory, true );
        supplier.supply();
    }
}
