package org.neo4j.integration.cli.mysql;

import java.nio.file.Files;
import java.nio.file.Path;

import org.neo4j.integration.commands.mysql.ExportFromMySqlEvents;
import org.neo4j.integration.util.CliRunner;

import static java.lang.String.format;

public class ExportMySqlEventHandler implements ExportFromMySqlEvents
{
    @Override
    public void onExportingToCsv( Path csvDirectory )
    {
        CliRunner.print( "Exporting from MySQL to CSV..." );
        CliRunner.print( format( "CSV directory: %s", csvDirectory ) );
    }

    @Override
    public void onCreatingNeo4jStore()
    {
        CliRunner.print( "Creating Neo4j store from CSV..." );
    }

    @Override
    public void onExportComplete( Path destinationDirectory )
    {
        CliRunner.printResult( destinationDirectory );
        Path badLogLocation = destinationDirectory.getParent().resolve( "bad.log" );
        if ( Files.exists( badLogLocation ) )
        {
            CliRunner.print( "There were bad entries which were skipped and logged into " + badLogLocation.toString() );
        }
    }
}
