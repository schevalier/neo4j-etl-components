package org.neo4j.integration.cli.mysql;

import org.neo4j.integration.commands.mysql.GenerateMetadataMappingEvents;
import org.neo4j.integration.util.CliRunner;

public class GenerateMetadataMappingEventHandler implements GenerateMetadataMappingEvents
{
    @Override
    public void onGeneratingMetadataMapping()
    {
        CliRunner.print( "Creating MySQL to CSV mappings..." );
    }

    @Override
    public void onMetadataMappingGenerated()
    {
        // Do nothing
    }
}
