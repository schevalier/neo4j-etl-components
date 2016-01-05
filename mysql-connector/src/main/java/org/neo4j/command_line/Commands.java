package org.neo4j.command_line;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.neo4j.io.StreamEventHandler;
import org.neo4j.io.StreamSink;
import org.neo4j.utils.Loggers;

public class Commands
{
    public static Builder.WorkingDirectory builder( String... commands )
    {
        return new CommandsBuilder( commands );
    }

    public static Commands commands( String... commands )
    {
        return new CommandsBuilder( commands ).build();
    }

    private final List<String> commands;
    private final Optional<Path> workingDirectory;
    private final Result.Evaluator resultEvaluator;
    private final long timeoutMillis;
    private final Map<String, String> extraEnvironment;
    private final ProcessBuilder.Redirect stdInRedirect;
    private final StreamEventHandler stdOutEventHandler;
    private final StreamEventHandler stdErrEventHandler;

    Commands( CommandsBuilder builder )
    {
        if ( builder.commands.isEmpty() )
        {
            throw new IllegalArgumentException( "Commands cannot be empty" );
        }

        this.commands = builder.commands;
        this.workingDirectory = builder.workingDirectory;
        this.resultEvaluator = builder.resultEvaluator;
        this.timeoutMillis = builder.timeoutMillis;
        this.extraEnvironment = builder.extraEnvironment;
        this.stdInRedirect = builder.stdInRedirect;
        this.stdOutEventHandler = builder.stdOutEventHandler;
        this.stdErrEventHandler = builder.stdErrEventHandler;
    }

    public ProcessHandle execute() throws Exception
    {
        Loggers.Default.log( Level.FINE, "Executing command '{0}'", programAndArguments() );

        ProcessBuilder processBuilder = new ProcessBuilder( commands );
        if ( workingDirectory.isPresent() )
        {
            processBuilder.directory( workingDirectory.map( Path::toFile ).get() );
        }
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

        return new ProcessHandle(
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
            ResultEvaluator workingDirectory( Path workingDirectory );

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
