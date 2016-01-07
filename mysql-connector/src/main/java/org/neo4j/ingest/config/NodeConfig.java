package org.neo4j.ingest.config;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import org.neo4j.utils.Preconditions;

public class NodeConfig
{
    public static Builder.SetFirstInputFile builder()
    {
        return new NodeConfigBuilder();
    }

    private final Collection<Path> files;
    private final Collection<String> labels;

    NodeConfig( NodeConfigBuilder builder )
    {
        this.files = Collections.unmodifiableCollection(
                Preconditions.requireNonEmptyCollection( builder.files, "Files cannot be empty" ));
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
        interface SetFirstInputFile
        {
            Builder addInputFile( Path file );
        }

        Builder addInputFile( Path file );

        Builder addLabel( String label );

        NodeConfig build();
    }
}
