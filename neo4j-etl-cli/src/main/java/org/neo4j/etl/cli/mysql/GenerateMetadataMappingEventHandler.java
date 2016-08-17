package org.neo4j.etl.cli.mysql;

import org.neo4j.etl.commands.mysql.GenerateMetadataMappingEvents;
import org.neo4j.etl.util.CliRunner;

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
