package org.neo4j.ingest;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

public class NodeConfig
{
    public static Builder.FirstInputFile builder()
    {
        return new NodeConfigBuilder();
    }

    private final Collection<Path> files;
    private final Collection<String> labels;

    NodeConfig( NodeConfigBuilder builder )
    {
        if (builder.files.isEmpty())
        {
            throw new IllegalArgumentException( "Files cannot be empty" );
        }

        this.files = Collections.unmodifiableCollection( builder.files );
        this.labels = Collections.unmodifiableCollection( builder.labels );
    }

    public Collection<Path> files()
    {
        return files;
    }

    public Collection<String> labels()
    {
        return labels;
    }

    public interface Builder
    {
        interface FirstInputFile
        {
            Builder addInputFile( Path file );
        }

        Builder addInputFile( Path file );

        Builder addLabel( String label );

        NodeConfig build();
    }
}
