package org.neo4j.integration.environment;

import java.io.IOException;

import org.neo4j.integration.util.Supplier;

public class EnvironmentSupplier implements Supplier<Environment>
{
    private final ImportToolDirectorySupplier importToolDirectorySupplier;
    private final DestinationDirectorySupplier destinationDirectorySupplier;
    private final CsvDirectorySupplier csvDirectorySupplier;

    public EnvironmentSupplier( ImportToolDirectorySupplier importToolDirectorySupplier,
                                DestinationDirectorySupplier destinationDirectorySupplier,
                                CsvDirectorySupplier csvDirectorySupplier )
    {
        this.importToolDirectorySupplier = importToolDirectorySupplier;
        this.destinationDirectorySupplier = destinationDirectorySupplier;
        this.csvDirectorySupplier = csvDirectorySupplier;
    }

    @Override
    public Environment supply() throws IOException
    {
        return new Environment(
                importToolDirectorySupplier.supply(),
                destinationDirectorySupplier.supply(),
                csvDirectorySupplier.supply() );
    }
}
