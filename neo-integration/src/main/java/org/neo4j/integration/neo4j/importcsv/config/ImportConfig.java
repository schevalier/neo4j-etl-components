package org.neo4j.integration.neo4j.importcsv.config;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import org.neo4j.integration.process.Commands;
import org.neo4j.integration.process.CommandsSupplier;
import org.neo4j.integration.neo4j.importcsv.fields.IdType;
import org.neo4j.integration.util.OperatingSystem;
import org.neo4j.integration.util.Preconditions;

public class ImportConfig implements CommandsSupplier
{
    public static Builder.SetImportToolDirectory builder()
    {
        return new ImportConfigBuilder();
    }

    public static final String IMPORT_TOOL = OperatingSystem.isWindows() ? "Neo4jImport.bat" : "neo4j-import";

    private final Path importToolDirectory;
    private final Path destination;
    private final Formatting formatting;
    private final IdType idType;
    private final Collection<NodeConfig> nodes;
    private final Collection<RelationshipConfig> relationships;

    ImportConfig( ImportConfigBuilder builder )
    {
        this.importToolDirectory = Preconditions.requireNonNull( builder.importToolDirectory, "ImportToolDirectory" );
        this.destination = Preconditions.requireNonNull( builder.destination, "Destination" );
        this.formatting = Preconditions.requireNonNull( builder.formatting, "Formatting" );
        this.idType = Preconditions.requireNonNull( builder.idType, "IdType" );
        this.nodes = builder.nodes;
        this.relationships = builder.relationships;
    }

    @Override
    public void addCommandsTo( Commands.Builder.SetCommands commands )
    {
        commands.addCommand( importToolDirectory.resolve( IMPORT_TOOL ).toString() );

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
        commands.addCommand( formatting.quote().argValue() );

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

        Builder graphDataConfig( GraphConfig graphConfig );

        Builder addNodeConfig( NodeConfig nodeConfig );

        Builder addRelationshipConfig( RelationshipConfig relationshipConfig );

        ImportConfig build();
    }
}
