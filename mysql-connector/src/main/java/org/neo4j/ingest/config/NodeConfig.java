package org.neo4j.ingest.config;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import org.neo4j.command_line.Commands;
import org.neo4j.command_line.CommandsSupplier;
import org.neo4j.utils.Preconditions;

import static java.lang.String.format;

import static org.neo4j.utils.StringListBuilder.stringList;

public class NodeConfig implements CommandsSupplier
{
    public static Builder.SetInputFiles builder()
    {
        return new NodeConfigBuilder();
    }

    private final Collection<Path> files;
    private final Collection<String> labels;

    NodeConfig( NodeConfigBuilder builder )
    {
        this.files = Collections.unmodifiableCollection(
                Preconditions.requireNonEmptyCollection( builder.files, "Files" ) );
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

    @Override
    public void addCommandsTo( Commands.Builder.SetCommands commands )
    {
        commands.addCommand( labels.isEmpty() ? "--nodes" : format( "--nodes[:%s]", stringList( labels, ":" ) ) );
        commands.addCommand( format( "\"%s\"", stringList( files, ",", item -> item.toAbsolutePath().toString() ) ) );
    }

    public interface Builder
    {
        interface SetInputFiles
        {
            Builder addInputFile( Path file );

            Builder addInputFiles( Collection<Path> files );
        }

        Builder addInputFile( Path file );

        Builder addInputFiles( Collection<Path> files );

        Builder addLabel( String label );

        NodeConfig build();
    }
}
