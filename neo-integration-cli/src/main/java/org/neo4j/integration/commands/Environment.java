package org.neo4j.integration.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.neo4j.integration.neo4j.importcsv.config.ImportConfig;
import org.neo4j.integration.util.FileUtils;

public class Environment
{
    private final Path importToolDirectory;
    private final Path destinationDirectory;
    private final boolean overwriteByForce;
    private final Path csvRootDirectory;

    public Environment( Path importToolDirectory,
                        Path destinationDirectory,
                        Path csvRootDirectory,
                        boolean overwriteByForce )
    {
        this.importToolDirectory = importToolDirectory;
        this.destinationDirectory = destinationDirectory;
        this.overwriteByForce = overwriteByForce;
        this.csvRootDirectory = csvRootDirectory;
    }

    public Path prepare() throws IOException
    {
        ensureImportToolExists( importToolDirectory );
        deleteDestinationDirectory( destinationDirectory, overwriteByForce );
        return createCsvDirectory( csvRootDirectory );
    }

    public void ensureImportToolExists( Path importToolDirectory )
    {
        Path importTool = importToolDirectory.resolve( ImportConfig.IMPORT_TOOL );
        if ( Files.notExists( importTool ) )
        {
            throw new IllegalArgumentException( String.format( "Unable to find import tool: %s", importTool ) );
        }
    }

    public Path createCsvDirectory( Path csvRootDirectory ) throws IOException
    {
        Files.createDirectories( csvRootDirectory );
        int index = 1;

        Path csvDirectory = csvRootDirectory.resolve( String.format( "csv-%03d", index++ ) );

        while ( Files.exists( csvDirectory ) )
        {
            csvDirectory = csvRootDirectory.resolve( String.format( "csv-%03d", index++ ) );
        }

        Files.createDirectories( csvDirectory );

        return csvDirectory;
    }

    public void deleteDestinationDirectory( Path destinationDirectory, boolean overwriteByForce ) throws IOException
    {
        if ( Files.exists( destinationDirectory ) && !overwriteByForce )
        {
            throw new IllegalStateException( "Destination already exists" );
        }
        else
        {
            FileUtils.deleteRecursively( destinationDirectory );
        }
    }

    public Path importToolDirectory()
    {
        return importToolDirectory;
    }

    public Path destinationDirectory()
    {
        return destinationDirectory;
    }
}
