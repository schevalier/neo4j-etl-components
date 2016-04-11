package org.neo4j.integration.environment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.neo4j.integration.neo4j.importcsv.config.ImportConfig;
import org.neo4j.integration.util.Supplier;

public class ImportToolDirectorySupplier implements Supplier<Path>
{
    private final Path importToolDirectory;

    public ImportToolDirectorySupplier( Path importToolDirectory )
    {
        this.importToolDirectory = importToolDirectory;
    }

    @Override
    public Path supply( ) throws IOException
    {
        Path importTool = importToolDirectory.resolve( ImportConfig.IMPORT_TOOL );
        if ( Files.notExists( importTool ) )
        {
            throw new IllegalArgumentException( String.format( "Unable to find import tool: %s", importTool ) );
        }
        return importToolDirectory;
    }
}
