package org.neo4j.integration.neo4j.importcsv.config;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import org.neo4j.integration.cli.Commands;
import org.neo4j.integration.cli.CommandsSupplier;
import org.neo4j.integration.util.Preconditions;

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
    private final Collection<RelationshipConfig> relationships;

    ImportConfig( ImportConfigBuilder builder )
    {
        this.importToolDirectory = Preconditions.requireNonNull( builder.importToolDirectory, "Import tool directory" );
        this.destination = Preconditions.requireNonNull( builder.destination, "Destination" );
        this.formatting = Preconditions.requireNonNull( builder.formatting, "Formatting" );
        this.idType = Preconditions.requireNonNull( builder.idType, "Id type" );
        this.nodes = Collections.unmodifiableCollection( builder.nodes );
        this.relationships = Collections.unmodifiableCollection( builder.relationships );
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

        for ( RelationshipConfig relationship : relationships )
        {
            relationship.addCommandsTo( commands );
        }

        commands.addCommand( "--delimiter" );
        commands.addCommand( formatting.delimiter().description() );

        commands.addCommand( "--array-delimiter" );
        commands.addCommand( formatting.arrayDelimiter().description() );

        commands.addCommand( "--quote" );
        commands.addCommand( formatting.quoteCharacter().value() );

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

        Builder graphDataConfig( GraphDataConfig graphDataConfig );

        Builder addNodeConfig( NodeConfig nodeConfig );

        Builder addRelationshipConfig( RelationshipConfig relationshipConfig );

        ImportConfig build();
    }
}
