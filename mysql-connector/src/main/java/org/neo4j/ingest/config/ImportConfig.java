package org.neo4j.ingest.config;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.command_line.Commands;
import org.neo4j.command_line.CommandsSupplier;
import org.neo4j.utils.Preconditions;

import static java.lang.String.format;

public class ImportConfig implements CommandsSupplier
{
    public static Builder.SetImportToolDirectory builder()
    {
        return new ImportConfigBuilder();
    }

    private final Path importToolDirectory;
    private final Path destination;
    private final Formatting formatting;
    private final IdType idType;
    private final Collection<NodeConfig> nodes;

    ImportConfig( ImportConfigBuilder builder )
    {
        this.importToolDirectory = Preconditions.requireNonNull( builder.importToolDirectory, "Import tool directory" );
        this.destination = Preconditions.requireNonNull( builder.destination, "Destination" );
        this.formatting = Preconditions.requireNonNull( builder.formatting, "Formatting" );
        this.idType = builder.idType;
        this.nodes = builder.nodes;
    }

    @Override
    public void addCommandsTo( Commands.Builder.SetCommands commands )
    {
        commands.addCommand( importToolDirectory.resolve( "neo4j-import" ).toString() );

        commands.addCommand( "--into" );
        commands.addCommand( destination.toAbsolutePath().toString() );

        for ( NodeConfig node : nodes )
        {
            node.addCommandsTo( commands );
        }

        commands.addCommand( "--delimiter" );
        commands.addCommand( formatting.delimiter().description() );

        commands.addCommand( "--array-delimiter" );
        commands.addCommand( formatting.arrayDelimiter().description() );

        commands.addCommand( "--quote" );
        commands.addCommand( formatting.quote() );

        commands.addCommand( "--id-type" );
        commands.addCommand( idType.name().toUpperCase() );
    }

    public interface Builder
    {
        interface SetImportToolDirectory
        {
            SetDestination importToolDirectory( Path directory );
        }

        interface SetDestination
        {
            SetFormatting destination( Path directory );
        }

        interface SetFormatting
        {
            SetIdType formatting( Formatting formatting );
        }

        interface SetIdType
        {
            Builder idType( IdType idType );
        }

        Builder addNodeConfig( NodeConfig nodeConfig );

        ImportConfig build();
    }
}
