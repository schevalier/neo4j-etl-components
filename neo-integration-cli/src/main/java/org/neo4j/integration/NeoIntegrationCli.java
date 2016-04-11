package org.neo4j.integration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.logging.LogManager;

import io.airlift.airline.Cli;
import io.airlift.airline.help.Help;

import org.neo4j.integration.cli.mysql.CreateCsvResourcesCli;
import org.neo4j.integration.cli.mysql.ExportFromMySqlCli;
import org.neo4j.integration.util.CliRunner;

import static java.util.Arrays.asList;

public class NeoIntegrationCli
{
    public static void main( String[] args )
    {
        try
        {
            for ( String arg : args )
            {
                if ( arg.equalsIgnoreCase( "--debug" ) )
                {
                    LogManager.getLogManager().readConfiguration(
                            NeoIntegrationCli.class.getResourceAsStream( "/debug-logging.properties" ) );
                }
                else
                {
                    LogManager.getLogManager().readConfiguration(
                            NeoIntegrationCli.class.getResourceAsStream( "/minimal-logging.properties" ) );
                }
            }
        }
        catch ( IOException e )
        {
            System.err.println( "Error in loading configuration" );
            e.printStackTrace( System.err );
        }

        CliRunner.run( parser(), args );
    }

    static Collection<String> executeMainReturnSysOut( String[] args )
    {
        PrintStream oldOut = System.out;

        ByteArrayOutputStream newOut = new ByteArrayOutputStream();
        System.setOut( new PrintStream( newOut ) );

        CliRunner.run( parser(), args, CliRunner.OnCommandFinished.DoNothing );

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

    private static Cli<Runnable> parser()
    {
        Cli.CliBuilder<Runnable> builder = Cli.<Runnable>builder( "neo-integration" )
                .withDescription( "Neo4j integration tools." )
                .withDefaultCommand( Help.class )
                .withCommand( Help.class );

        builder.withGroup( "mysql" )
                .withDescription( "MySQL export tools." )
                .withDefaultCommand( Help.class )
                .withCommand( ExportFromMySqlCli.class )
                .withCommand( CreateCsvResourcesCli.class )
                .withCommand( Help.class );

        return builder.build();
    }
}
