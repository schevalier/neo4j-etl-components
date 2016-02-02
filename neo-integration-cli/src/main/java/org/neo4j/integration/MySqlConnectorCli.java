package org.neo4j.integration;

import java.io.IOException;
import java.util.logging.LogManager;

import io.airlift.airline.Cli;
import io.airlift.airline.help.Help;

import org.neo4j.integration.commands.ExportFromMySqlCommand;
import org.neo4j.integration.util.CliRunner;

public class MySqlConnectorCli
{
    static
    {
        try
        {
            LogManager.getLogManager().readConfiguration(
                    MySqlConnectorCli.class.getResourceAsStream( "/logging.properties" ) );
        }
        catch ( IOException e )
        {
            System.err.println( "Error in loading configuration" );
            e.printStackTrace( System.err );
        }
    }

    public static void main( String[] args )
    {
        Cli.CliBuilder<Runnable> builder = Cli.<Runnable>builder( "neo-integration" )
                .withDescription( "Neo4j integration tools." )
                .withDefaultCommand( Help.class )
                .withCommand( ExportFromMySqlCommand.class )
                .withCommand( Help.class );
        CliRunner.run( builder.build(), args );
    }
}
