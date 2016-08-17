package org.neo4j.etl.neo4j.importcsv.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

class RelationshipConfigBuilder implements RelationshipConfig.Builder, RelationshipConfig.Builder.SetInputFiles
{
    final Collection<Path> files = new ArrayList<>();
    String type;

    @Override
    public RelationshipConfig.Builder addInputFile( Path file )
    {
        files.add( file );
        return this;
    }

    @Override
    public RelationshipConfig.Builder addInputFiles( Collection<Path> files )
    {
        this.files.addAll( files );
        return this;
    }

    @Override
    public RelationshipConfig.Builder type( String type )
    {
        this.type = type;
        return this;
    }

    @Override
    public RelationshipConfig build()
    {
        return new RelationshipConfig( this );
    }
}
