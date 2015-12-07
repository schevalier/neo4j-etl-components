package org.neo4j.command_line;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.neo4j.io.StreamEventHandler;
import org.neo4j.io.StreamSink;

import static java.lang.String.format;

public class Commands
{
    private static final Logger LOG = LoggerFactory.getLogger( Commands.class );

    public static Builder.WorkingDirectory forCommands( String... commands )
    {
        return new CommandsBuilder( commands );
    }

    private final List<String> commands;
    private final File workingDirectory;
    private final Result.Evaluator resultEvaluator;
    private final long timeoutMillis;
    private final Map<String, String> extraEnvironment;
    private final ProcessBuilder.Redirect stdInRedirect;
    private final StreamEventHandler stdOutEventHandler;
    private final StreamEventHandler stdErrEventHandler;

    Commands( List<String> commands, File workingDirectory,
              Result.Evaluator resultEvaluator,
              long timeoutMillis,
              Map<String, String> extraEnvironment,
              ProcessBuilder.Redirect stdInRedirect,
              StreamEventHandler stdOutEventHandler,
              StreamEventHandler stdErrEventHandler )
    {
        this.commands = commands;
        this.workingDirectory = workingDirectory;
        this.resultEvaluator = resultEvaluator;
        this.timeoutMillis = timeoutMillis;
        this.extraEnvironment = extraEnvironment;
        this.stdInRedirect = stdInRedirect;
        this.stdOutEventHandler = stdOutEventHandler;
        this.stdErrEventHandler = stdErrEventHandler;
    }

    public Result execute() throws Exception
    {
        Process process = null;

        try
        {
            LOG.debug( "Executing command '{}'", programAndArguments() );

            ProcessBuilder processBuilder = new ProcessBuilder( commands ).directory( workingDirectory );
            processBuilder.environment().putAll( extraEnvironment );
            processBuilder.redirectInput( stdInRedirect );

            long startTime = System.currentTimeMillis();

            process = processBuilder.start();

            new StreamSink( process.getInputStream(), stdOutEventHandler ).start();
            new StreamSink( process.getErrorStream(), stdErrEventHandler ).start();

            Timer timer = new Timer();
            DestroyProcessOnTimeout timerTask = new DestroyProcessOnTimeout( process );
            if ( timeoutMillis > 0 )
            {
                timer.schedule( timerTask, timeoutMillis );
            }

            int exitValue = process.waitFor();
            timer.cancel();

            long endTime = System.currentTimeMillis();

            Result result = new Result(
                    exitValue,
                    stdOutEventHandler.awaitContents( 5, TimeUnit.SECONDS ).toString(),
                    stdErrEventHandler.awaitContents( 5, TimeUnit.SECONDS ).toString(),
                    endTime - startTime );

            if ( timerTask.timedOut() )
            {
                throw new TimeoutException( format( "Command failed to complete in a timely manner " +
                        "[TimeoutMillis: %s, %s]", timeoutMillis, result ) );
            }

            if ( !resultEvaluator.isValid( result ) )
            {
                throw new Exception( format( "Command failed [Command: '%s', %s]", programAndArguments(), result ) );
            }

            LOG.trace( "Command finished [Command: '{}', {}]", programAndArguments(), result );

            return result;
        }
        catch ( InterruptedException e )
        {
            LOG.info( "Cancelling command [Command: {}]", programAndArguments() );
            return null;
        }
        catch ( IOException | TimeoutException e )
        {
            throw new Exception( format( "Command failed [Command: '%s']", programAndArguments() ), e );
        }
        finally
        {
            if ( process != null )
            {
                process.destroy();
            }
        }
    }

    public String programAndArguments()
    {
        StringBuilder builder = new StringBuilder();
        for ( String command : commands )
        {
            if ( builder.length() > 0 )
            {
                builder.append( " " );
            }
            builder.append( command );
        }
        return builder.toString();
    }

    public interface Builder
    {
        interface WorkingDirectory
        {
            ResultEvaluator workingDirectory( File workingDirectory );

            ResultEvaluator inheritWorkingDirectory();
        }

        interface ResultEvaluator
        {
            TimeoutMillis commandResultEvaluator( Result.Evaluator resultEvaluator );

            TimeoutMillis failOnNonZeroExitValue();

            TimeoutMillis ignoreFailures();
        }

        interface TimeoutMillis
        {
            Environment timeout( long timeout, TimeUnit unit );

            Environment noTimeout();
        }

        interface Environment
        {
            Redirection inheritEnvironment();

            Redirection augmentEnvironment( Map<String, String> extra );
        }

        interface Redirection
        {
            Redirection redirectStdInFrom( ProcessBuilder.Redirect redirection );

            Redirection redirectStdOutTo( StreamEventHandler streamEventHandler );

            Redirection redirectStdErrTo( StreamEventHandler streamEventHandler );

            Commands build();
        }
    }
}
