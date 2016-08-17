package org.neo4j.etl.environment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.neo4j.etl.util.Supplier;

public class CsvDirectorySupplier implements Supplier<Path>
{
    private final Path csvRootDirectory;

    public CsvDirectorySupplier( Path csvRootDirectory )
    {
        this.csvRootDirectory = csvRootDirectory;
    }

    @Override
    public Path supply() throws IOException
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
}
