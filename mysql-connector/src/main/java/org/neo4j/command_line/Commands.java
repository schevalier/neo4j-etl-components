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

import org.neo4j.io.StreamContents;

import static java.lang.String.format;

public class Commands
{
    private static final Logger LOG = LoggerFactory.getLogger( Commands.class );

    public static Builder.WorkingDirectory forCommands( String... commands )
    {
        return new CommandsBuilder( commands );
    }

    private final File workingDirectory;
    private final List<String> commands;
    private final Result.Evaluator resultEvaluator;
    private final long timeoutMillis;
    private final Map<String, String> extraEnvironment;
    private final ProcessBuilder.Redirect stdInRedirect;
    private final ProcessConfigurator stdOutConfigurator;
    private final ProcessConfigurator stdErrConfigurator;

    Commands( File workingDirectory,
              List<String> commands,
              Result.Evaluator resultEvaluator,
              long timeoutMillis,
              Map<String, String> extraEnvironment,
              ProcessBuilder.Redirect stdInRedirect,
              ProcessConfigurator stdOutConfigurator,
              ProcessConfigurator stdErrConfigurator )
    {
        this.workingDirectory = workingDirectory;
        this.commands = commands;
        this.resultEvaluator = resultEvaluator;
        this.timeoutMillis = timeoutMillis;
        this.extraEnvironment = extraEnvironment;
        this.stdInRedirect = stdInRedirect;
        this.stdOutConfigurator = stdOutConfigurator;
        this.stdErrConfigurator = stdErrConfigurator;
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

            stdOutConfigurator.configure( processBuilder );
            stdErrConfigurator.configure( processBuilder );

            long startTime = System.currentTimeMillis();

            process = processBuilder.start();

            StreamContents stdout = stdOutConfigurator.start( process );
            StreamContents stderr = stdErrConfigurator.start( process );

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
                    stdout.value(),
                    stderr.value(),
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
            RedirectingStdIn inheritEnvironment();

            RedirectingStdIn augmentEnvironment( Map<String, String> extra );
        }

        interface RedirectingStdIn
        {
            Builder noRedirection();

            RedirectingStdOut doNotRedirectStdIn();

            RedirectingStdOut redirectStdInFrom( ProcessBuilder.Redirect redirection );
        }

        interface RedirectingStdOut
        {
            RedirectingStdErr doNotRedirectStdOut();

            RedirectingStdErr redirectStdOutTo( File file );

            RedirectingStdErr logStdOut( Logger log );
        }

        interface RedirectingStdErr
        {
            Builder doNotRedirectStdErr();

            Builder redirectStdErrTo( File file );

            Builder logStdErr( Logger log );
        }

        Commands build();
    }
}
