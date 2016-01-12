package org.neo4j.ingest.config;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.neo4j.command_line.Commands;
import org.neo4j.command_line.CommandsSupplier;
import org.neo4j.utils.Preconditions;
import org.neo4j.utils.Strings;

import static java.lang.String.format;

import static org.neo4j.utils.StringListBuilder.stringList;

public class RelationshipConfig implements CommandsSupplier, GraphDataConfigSupplier
{
    public static Builder.SetInputFiles builder()
    {
        return new RelationshipConfigBuilder();
    }

    private final Collection<Path> files;
    private final Optional<String> type;

    public RelationshipConfig( RelationshipConfigBuilder builder )
    {
        this.files = Collections.unmodifiableCollection(
                Preconditions.requireNonEmptyCollection( builder.files, "Files" ) );
        this.type = Optional.ofNullable( Strings.orNull( builder.type ) );
    }

    @Override
    public void addCommandsTo( Commands.Builder.SetCommands commands )
    {
        commands.addCommand( type.isPresent() ? format( "--relationships:%s", type.get() ) : "--relationships" );
        commands.addCommand( format( "%s", stringList( files, ",", item -> item.toAbsolutePath().toString() ) ) );
    }

    @Override
    public void addGraphDataConfigTo( ImportConfig.Builder importConfig )
    {
        importConfig.addRelationshipConfig( this );
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

        Builder type( String type );

        RelationshipConfig build();
    }
}
