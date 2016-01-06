package org.neo4j.ingest;

import java.nio.file.Path;

class ImportConfigBuilder implements ImportConfig.Builder.Destination, ImportConfig.Builder
{
    Path destination;
    String delimiter = ",";
    String arrayDelimiter = ";";
    String quote = "\"";

    @Override
    public ImportConfig.Builder destination( Path directory )
    {
        this.destination = directory;
        return this;
    }

    @Override
    public ImportConfig.Builder delimiter( String delimiter )
    {
        this.delimiter = delimiter;
        return this;
    }

    @Override
    public ImportConfig.Builder arrayDelimiter( String delimiter )
    {
        this.arrayDelimiter = delimiter;
        return this;
    }

    @Override
    public ImportConfig.Builder quote( String quote )
    {
        this.quote = quote;
        return this;
    }

    @Override
    public ImportConfig build()
    {
        return new ImportConfig( this );
    }
}
