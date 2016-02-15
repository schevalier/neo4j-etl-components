package org.neo4j.integration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.logging.LogManager;

import io.airlift.airline.Cli;
import io.airlift.airline.help.Help;

import org.neo4j.integration.commands.ExportFromMySqlCommand;
import org.neo4j.integration.util.CliRunner;

import static java.util.Arrays.asList;

public class NeoIntegrationCli
{
    static
    {
        try
        {
            LogManager.getLogManager().readConfiguration(
                    NeoIntegrationCli.class.getResourceAsStream( "/logging.properties" ) );
        }
        catch ( IOException e )
        {
            System.err.println( "Error in loading configuration" );
            e.printStackTrace( System.err );
        }
    }

    private static final Cli<Runnable> PARSER = Cli.<Runnable>builder( "neo-integration" )
            .withDescription( "Neo4j integration tools." )
            .withDefaultCommand( Help.class )
            .withCommand( ExportFromMySqlCommand.class )
            .withCommand( Help.class )
            .build();

    public static void main( String[] args )
    {
        CliRunner.run( PARSER, args );
    }

    static Collection<String> executeMainReturnSysOut( String[] args )
    {
        PrintStream oldOut = System.out;

        ByteArrayOutputStream newOut = new ByteArrayOutputStream();
        System.setOut( new PrintStream( newOut ) );

        CliRunner.run( PARSER, args, CliRunner.OnCommandFinished.DoNothing );

        try
        {
            return asList( newOut.toString( "UTF8" ).split( System.lineSeparator() ) );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
        finally
        {
            System.setOut( oldOut );
        }
    }
}
