package org.neo4j.integration.neo4j.importcsv.config;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import org.neo4j.integration.cli.Commands;
import org.neo4j.integration.cli.CommandsSupplier;
import org.neo4j.integration.util.Preconditions;

import static java.lang.String.format;

import static org.neo4j.integration.util.StringListBuilder.stringList;

public class NodeConfig implements CommandsSupplier, GraphDataConfigSupplier
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

    @Override
    public void addCommandsTo( Commands.Builder.SetCommands commands )
    {
        commands.addCommand( labels.isEmpty() ? "--nodes" : format( "--nodes:%s", stringList( labels, ":" ) ) );
        commands.addCommand( format( "%s", stringList( files, ",", item -> item.toAbsolutePath().toString() ) ) );
    }

    @Override
    public void addGraphDataConfigTo( ImportConfig.Builder importConfig )
    {
        importConfig.addNodeConfig( this );
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
