package org.neo4j.ingest;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

class NodeConfigBuilder implements NodeConfig.Builder.FirstInputFile, NodeConfig.Builder
{
    Collection<Path> files = new ArrayList<>();
    Collection<String> labels = new ArrayList<>();

    @Override
    public NodeConfig.Builder addInputFile( Path file )
    {
        files.add( file );
        return this;
    }

    @Override
    public NodeConfig.Builder addLabel( String label )
    {
        labels.add( label );
        return this;
    }

    @Override
    public NodeConfig build()
    {
        return new NodeConfig( this );
    }
}
