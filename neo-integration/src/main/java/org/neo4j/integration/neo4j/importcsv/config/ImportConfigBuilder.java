package org.neo4j.integration.neo4j.importcsv.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.fields.IdType;

class ImportConfigBuilder implements ImportConfig.Builder.SetImportToolDirectory,
        ImportConfig.Builder.SetImportToolOptions,
        ImportConfig.Builder.SetDestination,
        ImportConfig.Builder.SetFormatting,
        ImportConfig.Builder.SetIdType,
        ImportConfig.Builder
{
    Path importToolDirectory;
    Path destination;
    Formatting formatting;
    ImportToolOptions importToolOptions;
    IdType idType = IdType.String;
    final Collection<NodeConfig> nodes = new ArrayList<>();
    final Collection<RelationshipConfig> relationships = new ArrayList<>();

    @Override
    public SetImportToolOptions importToolDirectory( Path directory )
    {
        this.importToolDirectory = directory;
        return this;
    }

    @Override
    public SetDestination importToolOptions( ImportToolOptions importToolOptions )
    {
        this.importToolOptions = importToolOptions;
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
    public ImportConfig.Builder addRelationshipConfig( RelationshipConfig relationshipConfig )
    {
        relationships.add( relationshipConfig );
        return this;
    }

    @Override
    public ImportConfig build()
    {
        return new ImportConfig( this );
    }
}
