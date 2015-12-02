package org.neo4j.command_line;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import org.neo4j.io.FileBasedStreamRecorder;
import org.neo4j.io.InMemoryStreamRecorder;

import static java.util.Arrays.asList;

class CommandsBuilder
        implements Commands.Builder.WorkingDirectory,
        Commands.Builder.ResultEvaluator,
        Commands.Builder.TimeoutMillis,
        Commands.Builder.Environment,
        Commands.Builder.RedirectingStdIn,
        Commands.Builder.RedirectingStdOut,
        Commands.Builder.RedirectingStdErr,
        Commands.Builder
{
    private final List<String> commands;
    private File workingDirectory;
    private org.neo4j.command_line.Result.Evaluator resultEvaluator;
    private long timeoutMillis;
    private Map<String, String> extraEnvironment = Collections.emptyMap();
    private ProcessConfigurator stdOutConfigurator;
    private ProcessConfigurator stdErrConfigurator;
    private ProcessBuilder.Redirect stdInRedirect;

    public CommandsBuilder( String... commands )
    {
        this.commands = asList( commands );
    }

    @Override
    public ResultEvaluator workingDirectory( File workingDirectory )
    {
        this.workingDirectory = workingDirectory;
        return this;
    }

    @Override
    public ResultEvaluator inheritWorkingDirectory()
    {
        return workingDirectory( null );
    }

    @Override
    public TimeoutMillis commandResultEvaluator( Result.Evaluator resultEvaluator )
    {
        this.resultEvaluator = resultEvaluator;
        return this;
    }

    @Override
    public TimeoutMillis failOnNonZeroExitValue()
    {
        this.resultEvaluator = Result.Evaluator.FAIL_IF_EXIT_VALUE_IS_NOT_ZERO;
        return this;
    }

    @Override
    public TimeoutMillis ignoreFailures()
    {
        this.resultEvaluator = Result.Evaluator.IGNORE_FALIURES;
        return this;
    }

    @Override
    public Environment timeout( long timeout, TimeUnit unit )
    {
        timeoutMillis = unit.toMillis( timeout );
        return this;
    }

    @Override
    public Environment noTimeout()
    {
        this.timeoutMillis = -1;
        return this;
    }

    @Override
    public RedirectingStdIn inheritEnvironment()
    {
        return this;
    }

    @Override
    public RedirectingStdIn augmentEnvironment( Map<String, String> extra )
    {
        this.extraEnvironment = extra;
        return this;
    }

    @Override
    public Commands.Builder noRedirection()
    {
        return doNotRedirectStdIn().doNotRedirectStdOut().doNotRedirectStdErr();
    }

    @Override
    public RedirectingStdOut doNotRedirectStdIn()
    {
        this.stdInRedirect = ProcessBuilder.Redirect.PIPE;
        return this;
    }

    @Override
    public RedirectingStdOut redirectStdInFrom( ProcessBuilder.Redirect redirection )
    {
        this.stdInRedirect = redirection;
        return this;
    }

    @Override
    public RedirectingStdErr doNotRedirectStdOut()
    {
        this.stdOutConfigurator = new ProcessConfigurator(
                new InMemoryStreamRecorder(),
                InMemoryStreamRecorder.StreamType.StdOut );
        return this;
    }

    @Override
    public RedirectingStdErr redirectStdOutTo( File file )
    {
        this.stdOutConfigurator = new ProcessConfigurator(
                new FileBasedStreamRecorder( file ),
                FileBasedStreamRecorder.StreamType.StdOut );
        return this;
    }

    @Override
    public RedirectingStdErr logStdOut( final Logger log )
    {
        this.stdOutConfigurator = new ProcessConfigurator(
                new InMemoryStreamRecorder(),
                InMemoryStreamRecorder.StreamType.StdOut );
        return this;
    }

    @Override
    public CommandsBuilder doNotRedirectStdErr()
    {
        this.stdErrConfigurator = new ProcessConfigurator(
                new InMemoryStreamRecorder(),
                InMemoryStreamRecorder.StreamType.StdErr );
        return this;
    }

    @Override
    public CommandsBuilder redirectStdErrTo( File file )
    {
        this.stdErrConfigurator = new ProcessConfigurator(
                new FileBasedStreamRecorder( file ),
                FileBasedStreamRecorder.StreamType.StdErr );
        return this;
    }

    @Override
    public CommandsBuilder logStdErr( final Logger log )
    {
        this.stdErrConfigurator = new ProcessConfigurator(
                new InMemoryStreamRecorder(),
                InMemoryStreamRecorder.StreamType.StdErr );
        return this;
    }

    @Override
    public Commands build()
    {
        return new Commands(
                workingDirectory,
                commands,
                resultEvaluator,
                timeoutMillis,
                extraEnvironment,
                stdInRedirect,
                stdOutConfigurator,
                stdErrConfigurator );
    }
}
