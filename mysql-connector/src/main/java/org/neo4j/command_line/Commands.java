package org.neo4j.command_line;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.neo4j.io.StreamEventHandler;
import org.neo4j.io.StreamSink;

public class Commands
{
    private static final Logger LOG = LoggerFactory.getLogger( Commands.class );

    public static Builder.WorkingDirectory builder( String... commands )
    {
        return new CommandsBuilder( commands );
    }

    public static Commands commands( String... commands )
    {
        return new CommandsBuilder( commands ).build();
    }

    private final List<String> commands;
    private final File workingDirectory;
    private final Result.Evaluator resultEvaluator;
    private final long timeoutMillis;
    private final Map<String, String> extraEnvironment;
    private final ProcessBuilder.Redirect stdInRedirect;
    private final StreamEventHandler stdOutEventHandler;
    private final StreamEventHandler stdErrEventHandler;

    Commands( List<String> commands,
              File workingDirectory,
              Result.Evaluator resultEvaluator,
              long timeoutMillis,
              Map<String, String> extraEnvironment,
              ProcessBuilder.Redirect stdInRedirect,
              StreamEventHandler stdOutEventHandler,
              StreamEventHandler stdErrEventHandler )
    {
        if (commands.isEmpty())
        {
            throw new IllegalArgumentException( "Commands cannot be empty" );
        }

        this.commands = commands;
        this.workingDirectory = workingDirectory;
        this.resultEvaluator = resultEvaluator;
        this.timeoutMillis = timeoutMillis;
        this.extraEnvironment = extraEnvironment;
        this.stdInRedirect = stdInRedirect;
        this.stdOutEventHandler = stdOutEventHandler;
        this.stdErrEventHandler = stdErrEventHandler;
    }

    public ResultHandle execute() throws Exception
    {
        LOG.debug( "Executing command '{}'", programAndArguments() );

        ProcessBuilder processBuilder = new ProcessBuilder( commands ).directory( workingDirectory );
        processBuilder.environment().putAll( extraEnvironment );
        processBuilder.redirectInput( stdInRedirect );

        long startTime = System.currentTimeMillis();

        Process process = processBuilder.start();

        new StreamSink( process.getInputStream(), stdOutEventHandler ).start();
        new StreamSink( process.getErrorStream(), stdErrEventHandler ).start();

        Timer timer = new Timer();

        DestroyProcessOnTimeout timerTask = new DestroyProcessOnTimeout( process, timer );
        if ( timeoutMillis > 0 )
        {
            timer.schedule( timerTask, timeoutMillis );
        }

        return new ResultHandle(
                programAndArguments(),
                process,
                resultEvaluator,
                timer,
                timerTask,
                timeoutMillis,
                startTime,
                stdOutEventHandler,
                stdErrEventHandler );
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

    long timeoutMillis()
    {
        return timeoutMillis;
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
