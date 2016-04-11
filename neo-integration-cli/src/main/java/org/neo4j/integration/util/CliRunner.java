package org.neo4j.integration.util;

import java.util.logging.Level;

import io.airlift.airline.Cli;
import io.airlift.airline.ParseException;

public class CliRunner
{
    public enum OnCommandFinished
    {
        DestroyJVM
                {
                    @Override
                    public void apply( int status )
                    {
                        System.exit( status );
                    }
                },
        DoNothing
                {
                    @Override
                    public void apply( int status )
                    {
                        // Do nothing
                    }
                };

        public abstract void apply( int status );
    }

    public static void run( Cli<Runnable> parser, String[] args )
    {
        run( parser, args, OnCommandFinished.DestroyJVM );
    }

    public static void run( Cli<Runnable> parser, String[] args, OnCommandFinished onCommandFinished )
    {
        try
        {
            parser.parse( args ).run();
            onCommandFinished.apply( 0 );
        }
        catch ( ParseException e )
        {
            Loggers.Cli.log( Level.SEVERE, "Illegal command", e );

            parser.parse( "help" ).run();
            onCommandFinished.apply( -1 );
        }
    }

    public static void print( Object message )
    {
        System.err.println( message );
    }

    public static void printResult( Object message )
    {
        System.out.println( message );
    }
}
