package org.neo4j.ingest.config;

import java.nio.file.Path;

class ImportConfigBuilder implements ImportConfig.Builder.SetImportToolDirectory,
        ImportConfig.Builder.SetDestination,
        ImportConfig.Builder
{
    Path importToolDirectory;
    Path destination;
    String delimiter = ",";
    String arrayDelimiter = ";";
    String quote = "\"";

    @Override
    public SetDestination importToolDirectory( Path directory )
    {
        this.importToolDirectory = directory;
        return this;
    }

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
