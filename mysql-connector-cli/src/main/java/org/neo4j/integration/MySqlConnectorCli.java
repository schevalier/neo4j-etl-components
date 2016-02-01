package org.neo4j.integration;

import io.airlift.airline.Cli;
import io.airlift.airline.help.Help;

import org.neo4j.integration.commands.ExportFromMySqlCommand;
import org.neo4j.integration.util.CliRunner;

public class MySqlConnectorCli
{
    public static void main( String[] args )
    {
        Cli.CliBuilder<Runnable> builder = Cli.<Runnable>builder( "export-to-neo" )
                .withDescription( "Export tools." )
                .withDefaultCommand( Help.class )
                .withCommand( ExportFromMySqlCommand.class )
                .withCommand( Help.class );
        CliRunner.run( builder.build(), args );

    }
}
