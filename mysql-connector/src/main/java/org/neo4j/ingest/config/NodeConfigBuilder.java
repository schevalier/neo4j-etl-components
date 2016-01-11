package org.neo4j.ingest.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

class NodeConfigBuilder implements NodeConfig.Builder.SetInputFiles, NodeConfig.Builder
{
    final Collection<Path> files = new ArrayList<>();
    final Collection<String> labels = new ArrayList<>();

    @Override
    public NodeConfig.Builder addInputFile( Path file )
    {
        files.add( file );
        return this;
    }

    @Override
    public NodeConfig.Builder addInputFiles( Collection<Path> files )
    {
        this.files.addAll( files );
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
