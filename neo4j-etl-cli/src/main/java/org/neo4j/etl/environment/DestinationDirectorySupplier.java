package org.neo4j.etl.environment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.neo4j.etl.util.FileUtils;
import org.neo4j.etl.util.Supplier;

public class DestinationDirectorySupplier implements Supplier<Path>
{
    private final Path destinationDirectory;
    private final boolean overwriteByForce;

    public DestinationDirectorySupplier( Path destinationDirectory, boolean overwriteByForce )
    {
        this.destinationDirectory = destinationDirectory;
        this.overwriteByForce = overwriteByForce;
    }

    @Override
    public Path supply() throws IOException
    {
        if ( Files.exists( destinationDirectory ) && !overwriteByForce )
        {
            throw new IllegalStateException(
                    "Destination directory already exists. Use --force flag to force delete this directory." );
        }
        else
        {
            FileUtils.deleteRecursively( destinationDirectory );
        }

        return destinationDirectory;
    }
}
