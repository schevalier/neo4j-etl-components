package org.neo4j.integration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.logging.LogManager;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.help.Help;

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

    static String executeMainReturnSysOut( String[] args )
    {
        PrintStream oldOut = System.out;

        ByteArrayOutputStream newOut = new ByteArrayOutputStream();
        System.setOut( new PrintStream( newOut ) );

        CliRunner.run( parser(), args, CliRunner.OnCommandFinished.DoNothing );

        try
        {
            return newOut.toString( "UTF8" );
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
        CliBuilder<Runnable> builder = Cli.<Runnable>builder( "neo-integration" )
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
