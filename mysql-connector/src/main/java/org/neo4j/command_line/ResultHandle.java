package org.neo4j.command_line;

import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.neo4j.io.StreamEventHandler;

import static java.lang.String.format;

public class ResultHandle implements AutoCloseable
{
    private static final Logger LOG = LoggerFactory.getLogger( ResultHandle.class );

    private final String programAndArguments;
    private final Process process;
    private final Result.Evaluator resultEvaluator;
    private final Timer timer;
    private final DestroyProcessOnTimeout timerTask;
    private final long timeoutMillis;
    private final long startTime;
    private final StreamEventHandler stdOutEventHandler;
    private final StreamEventHandler stdErrEventHandler;

    ResultHandle( String programAndArguments,
                         Process process,
                         Result.Evaluator resultEvaluator,
                         Timer timer,
                         DestroyProcessOnTimeout timerTask,
                         long timeoutMillis,
                         long startTime,
                         StreamEventHandler stdOutEventHandler,
                         StreamEventHandler stdErrEventHandler )
    {
        this.programAndArguments = programAndArguments;
        this.process = process;
        this.resultEvaluator = resultEvaluator;
        this.timer = timer;
        this.timerTask = timerTask;
        this.timeoutMillis = timeoutMillis;
        this.startTime = startTime;
        this.stdOutEventHandler = stdOutEventHandler;
        this.stdErrEventHandler = stdErrEventHandler;
    }

    public Result await() throws Exception
    {
        try
        {
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
                throw new Exception( format( "Command failed [Command: '%s', %s]", programAndArguments, result ) );

            }

            LOG.trace( "Command finished [Command: '{}', {}]", programAndArguments, result );

            return result;
        }
        catch ( InterruptedException e )
        {
            LOG.info( "Cancelling command [Command: {}]", programAndArguments );
            return null;
        }
        catch ( IOException | TimeoutException e )
        {
            throw new Exception( format( "Command failed [Command: '%s']", programAndArguments ), e );
        }
        finally
        {
            if ( process != null )
            {
                process.destroy();
            }
        }
    }

    public void terminate()
    {
        process.destroy();
    }

    @Override
    public void close() throws Exception
    {
        terminate();
    }
}
