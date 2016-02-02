package org.neo4j.integration.util;

import java.util.logging.Level;

import io.airlift.airline.Cli;
import io.airlift.airline.ParseException;

public class CliRunner
{
    public static void run( Cli<Runnable> parser, String[] args )
    {
        try
        {
            parser.parse( args ).run();
            System.exit( 0 );
        }
        catch ( ParseException e )
        {
            Loggers.Cli.log( Level.SEVERE, "Illegal command", e );

            parser.parse( "help" ).run();
            System.exit( -1 );
        }
    }
}
