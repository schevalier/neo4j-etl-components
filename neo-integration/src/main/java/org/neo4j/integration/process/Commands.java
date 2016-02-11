package org.neo4j.integration.process;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.neo4j.integration.io.StreamEventHandler;
import org.neo4j.integration.io.StreamSink;
import org.neo4j.integration.util.Loggers;
import org.neo4j.integration.util.Preconditions;

public class Commands
{
    public static Builder.SetCommands builder()
    {
        return new CommandsBuilder();
    }

    public static Builder.SetWorkingDirectory builder( String... commands )
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
        this.commands = Collections.unmodifiableList(
                Preconditions.requireNonEmptyList( builder.commands, "Commands" ) );
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
        Loggers.Default.log( Level.FINE, "Executing command ''{0}''", programAndArguments() );

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
        interface SetCommands
        {
            SetCommands addCommand( String command );

            SetResultEvaluator workingDirectory( Path workingDirectory );

            SetResultEvaluator inheritWorkingDirectory();
        }

        interface SetWorkingDirectory
        {
            SetResultEvaluator workingDirectory( Path workingDirectory );

            SetResultEvaluator inheritWorkingDirectory();
        }

        interface SetResultEvaluator
        {
            SetTimeout commandResultEvaluator( Result.Evaluator resultEvaluator );

            SetTimeout failOnNonZeroExitValue();

            SetTimeout ignoreFailures();
        }

        interface SetTimeout
        {
            SetEnvironment timeout( long timeout, TimeUnit unit );

            SetEnvironment noTimeout();
        }

        interface SetEnvironment
        {
            SetRedirection inheritEnvironment();

            SetRedirection augmentEnvironment( Map<String, String> extra );
        }

        interface SetRedirection
        {
            SetRedirection redirectStdInFrom( ProcessBuilder.Redirect redirection );

            SetRedirection redirectStdInFrom( Path path );

            SetRedirection redirectStdOutTo( StreamEventHandler streamEventHandler );

            SetRedirection redirectStdErrTo( StreamEventHandler streamEventHandler );

            Commands build();
        }
    }
}
