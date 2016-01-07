package org.neo4j.ingest;

import java.nio.file.Path;
import java.util.Objects;

public class ImportConfig
{
    public static final String TAB_DELIMITER = "TAB";

    public static Builder.Destination builder()
    {
        return new ImportConfigBuilder();
    }

    private final Path destination;
    private final String delimiter;
    private final String arrayDelimiter;
    private final String quote;

    ImportConfig( ImportConfigBuilder builder )
    {
        this.destination = Objects.requireNonNull( builder.destination, "Destination cannot be null" );
        this.delimiter = builder.delimiter;
        this.arrayDelimiter = builder.arrayDelimiter;
        this.quote = builder.quote;
    }

    public Path getDestination()
    {
        return destination;
    }

    public String delimiter()
    {
        return delimiter;
    }

    public String arrayDelimiter()
    {
        return arrayDelimiter;
    }

    public String quote()
    {
        return quote;
    }

    public interface Builder
    {
        interface Destination
        {
            Builder destination( Path directory );
        }

        Builder delimiter( String delimiter );

        Builder arrayDelimiter( String delimiter );

        Builder quote( String quote );

        ImportConfig build();
    }
}
