package org.neo4j.integration.process;

import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.io.StreamEventHandler;
import org.neo4j.integration.util.FutureUtils;
import org.neo4j.integration.util.Loggers;

import static java.lang.String.format;

/**
 * An {@link AwaitHandle} to a running process.
 */
public class ProcessHandle implements AwaitHandle<Result>, AutoCloseable
{
    private final String programAndArguments;
    private final Process process;
    private final Result.Evaluator resultEvaluator;
    private final Timer timer;
    private final DestroyProcessOnTimeout timerTask;
    private final long timeoutMillis;
    private final long startTime;
    private final StreamEventHandler stdOutEventHandler;
    private final StreamEventHandler stdErrEventHandler;

    ProcessHandle( String programAndArguments,
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

    /**
     * Wait for the process to complete.
     *
     * @return {@link Result}
     * @throws Exception if the process times out, the return value indicates failure, or an IO exception occurs
     */
    @Override
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
                        "[Command: '%s', TimeoutMillis: %s, %s]", programAndArguments, timeoutMillis, result ) );
            }

            if ( !resultEvaluator.isValid( result ) )
            {
                throw new CommandFailedException(
                        format( "Command failed [Command: '%s', %s]", programAndArguments, result ) );

            }

            Loggers.Default.log(
                    Level.FINER,
                    "Command finished [Command: ''{0}'', {1}]",
                    new Object[]{programAndArguments, result} );

            return result;
        }
        catch ( InterruptedException e )
        {
            Loggers.Default.log( Level.FINE, "Cancelling command [Command: ''{0}'']", programAndArguments );
            return null;
        }
        catch ( IOException e )
        {
            throw new CommandFailedException( format( "Command failed [Command: '%s']", programAndArguments ), e );
        }
        finally
        {
            terminate();
        }
    }

    /**
     * Wait for the process to complete or until the specified waiting time elapses. If the specified waiting time
     * elapses, this method will return null, and the process will continue executing.
     *
     * @return {@link Result} or null if the specified waiting time elapses
     * @throws Exception if the process times out before the specified waiting time elapses,
     *                   the return value indicates failure, or an IO exception occurs
     */
    @Override
    public Result await( long timeout, TimeUnit unit ) throws Exception
    {
        try
        {
            return process.waitFor( timeout, unit ) ? await() : null;
        }
        catch ( InterruptedException e )
        {
            Loggers.Default.log( Level.FINE, "Cancelling command [Command: ''{0}'']", programAndArguments );
            return null;
        }
        catch ( IOException e )
        {
            throw new CommandFailedException( format( "Command failed [Command: '%s']", programAndArguments ), e );
        }
    }

    /**
     * Returns a {@link CompletableFuture} that can be used to wait for the process to complete, poll the process, or
     * terminate the process.
     * <p/>
     * <p>A new thread is created for each {@link CompletableFuture} returned by this method.
     * <p/>
     * <p>Calling the future's {@link CompletableFuture#get()} method will cause the future to block until the
     * underlying process completes. When the process completes, {@code get()} returns the process {@link Result}.
     * If the process times out, {@code get()} throws an ExecutionException whose root cause is a TimeoutException.
     * <p/>
     * <p>Calling the future's {@link CompletableFuture#get(long, TimeUnit)} method will cause the future to block
     * until the underlying process completes or the specified waiting time elapses. If the specified waiting time
     * elapses before the underlying process completes, the underlying process continues executing, but
     * {@code get(long, Timeunit)} throws a TimeoutException.
     * <p/>
     * <p>Calling the future's {@link CompletableFuture#cancel(boolean)} method will cause the future to be cancelled
     * and the underlying process to be terminated.
     *
     * @return A {@link CompletableFuture<Result>}
     */
    @Override
    public CompletableFuture<Result> toFuture()
    {
        CompletableFuture<Result> future = FutureUtils.exceptionableFuture( this::await, r -> new Thread( r ).start() );
        return future.whenComplete( ( result, throwable ) -> {
            if ( future.isCancelled() )
            {
                terminate();
            }
        } );
    }

    /**
     * Terminate the underlying process.
     */
    public void terminate()
    {
        if ( process != null )
        {
            process.destroy();
        }
    }

    /**
     * Tests whether the underlying process has terminated.
     *
     * @return true if the underlying process has terminated, false if it is still running
     */
    public boolean isTerminated()
    {
        return process == null || process.isAlive();
    }

    /**
     * Closes the handle and terminates the underlying process.
     *
     * @throws Exception
     */
    @Override
    public void close() throws Exception
    {
        terminate();
    }
}
