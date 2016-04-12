package org.neo4j.integration.environment;

import java.io.IOException;

import org.neo4j.integration.neo4j.importcsv.config.ImportToolOptions;
import org.neo4j.integration.util.Supplier;

public class EnvironmentSupplier implements Supplier<Environment>
{
    private final ImportToolDirectorySupplier importToolDirectorySupplier;
    private final DestinationDirectorySupplier destinationDirectorySupplier;
    private final CsvDirectorySupplier csvDirectorySupplier;
    private String importToolOptionsFile;

    public EnvironmentSupplier( ImportToolDirectorySupplier importToolDirectorySupplier,
                                DestinationDirectorySupplier destinationDirectorySupplier,
                                CsvDirectorySupplier csvDirectorySupplier,
                                String importToolOptionsFile )
    {
        this.importToolDirectorySupplier = importToolDirectorySupplier;
        this.destinationDirectorySupplier = destinationDirectorySupplier;
        this.csvDirectorySupplier = csvDirectorySupplier;
        this.importToolOptionsFile = importToolOptionsFile;
    }

    @Override
    public Environment supply() throws IOException
    {
        return new Environment(
                importToolDirectorySupplier.supply(),
                destinationDirectorySupplier.supply(),
                csvDirectorySupplier.supply(),
                ImportToolOptions.initialiseFromFile( importToolOptionsFile ) );
    }

}
