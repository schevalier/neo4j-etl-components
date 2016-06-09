package org.neo4j.integration.cli.mysql;

import org.neo4j.integration.commands.mysql.CreateCsvResourcesEvents;
import org.neo4j.integration.util.CliRunner;

public class CreateCsvResourcesEventHandler implements CreateCsvResourcesEvents
{
    @Override
    public void onCreatingCsvResourcesFile()
    {
        CliRunner.print( "Creating MySQL to CSV mappings..." );
    }

    @Override
    public void onCsvResourcesCreated()
    {
        // Do nothing
    }
}
