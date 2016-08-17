package org.neo4j.etl.util;

import java.util.logging.Level;

import com.github.rvesse.airline.Cli;

import static java.lang.String.format;

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
        catch ( Exception e )
        {
            Loggers.Cli.log( Level.SEVERE,
                    format( "Illegal command (%s: %s)%n", e.getClass().getSimpleName(), e.getMessage() ) );

            parser.parse( "help" ).run();
            onCommandFinished.apply( -1 );
        }
    }

    public static void handleException( Exception e, boolean debug )
    {
        if ( debug )
        {
            e.printStackTrace( System.err );
        }
        else
        {
            print( format( "Command failed due to error (%s: %s). " +
                            "Rerun with --debug flag for detailed diagnostic information.",
                    e.getClass().getSimpleName(),
                    e.getMessage() ) );
        }
        System.exit( -1 );
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
