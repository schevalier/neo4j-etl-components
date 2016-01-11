package org.neo4j.ingest.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

class ImportConfigBuilder implements ImportConfig.Builder.SetImportToolDirectory,
        ImportConfig.Builder.SetDestination,
        ImportConfig.Builder.SetFormatting,
        ImportConfig.Builder.SetIdType,
        ImportConfig.Builder
{
    Path importToolDirectory;
    Path destination;
    Formatting formatting;
    IdType idType = IdType.String;
    Collection<NodeConfig> nodes = new ArrayList<>(  );

    @Override
    public SetDestination importToolDirectory( Path directory )
    {
        this.importToolDirectory = directory;
        return this;
    }

    @Override
    public ImportConfig.Builder.SetFormatting destination( Path directory )
    {
        this.destination = directory;
        return this;
    }

    @Override
    public ImportConfig.Builder.SetIdType formatting( Formatting formatting )
    {
        this.formatting = formatting;
        return this;
    }

    @Override
    public ImportConfig.Builder idType( IdType idType )
    {
        this.idType = idType;
        return this;
    }

    @Override
    public ImportConfig.Builder addNodeConfig( NodeConfig nodeConfig )
    {
        nodes.add( nodeConfig );
        return this;
    }

    @Override
    public ImportConfig build()
    {
        return new ImportConfig( this );
    }
}
