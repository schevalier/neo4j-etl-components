package org.neo4j.command_line;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.neo4j.io.InMemoryStreamRecorder;
import org.neo4j.io.StreamRecorder;

import static java.util.Arrays.asList;

class CommandsBuilder
        implements Commands.Builder.WorkingDirectory,
        Commands.Builder.ResultEvaluator,
        Commands.Builder.TimeoutMillis,
        Commands.Builder.Environment,
        Commands.Builder.Redirection,
        Commands.Builder
{
    private final List<String> commands;
    private File workingDirectory;
    private org.neo4j.command_line.Result.Evaluator resultEvaluator;
    private long timeoutMillis;
    private Map<String, String> extraEnvironment = Collections.emptyMap();
    private StreamRecorder stdOutRecorder = new InMemoryStreamRecorder();
    private StreamRecorder stdErrRecorder = new InMemoryStreamRecorder();
    private ProcessBuilder.Redirect stdInRedirect = ProcessBuilder.Redirect.PIPE;

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
    public Redirection inheritEnvironment()
    {
        return this;
    }

    @Override
    public Redirection augmentEnvironment( Map<String, String> extra )
    {
        this.extraEnvironment = extra;
        return this;
    }
    @Override
    public Redirection redirectStdInFrom( ProcessBuilder.Redirect redirection )
    {
        this.stdInRedirect = redirection;
        return this;
    }

    @Override
    public Redirection redirectStdOutTo( StreamRecorder streamRecorder )
    {
        this.stdOutRecorder = streamRecorder;
        return this;
    }

    @Override
    public Redirection redirectStdErrTo( StreamRecorder streamRecorder )
    {
        this.stdErrRecorder = streamRecorder;
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
                stdOutRecorder,
                stdErrRecorder );
    }
}
