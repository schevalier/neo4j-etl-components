package org.neo4j.utils;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FutureUtilsTest
{
    private static final Resource<Executor> EXECUTOR = new Resource<Executor>()
    {
        private final ExecutorService executorService = Executors.newFixedThreadPool( 3 );

        @Override
        public Executor get()
        {
            return executorService;
        }

        @Override
        public void close() throws Exception
        {
            executorService.shutdownNow();
        }
    };

    @Test
    public void shouldReturnFutureThatReturnsFutureValueIfNoneOfTheExceptionablesFail() throws ExecutionException,
            InterruptedException
    {
        // given
        String expectedValue = "result";

        CompletableFuture<Object> exceptionable1 = FutureUtils.exceptionableFuture( Object::new, EXECUTOR.get() );
        CompletableFuture<Object> exceptionable2 = FutureUtils.exceptionableFuture( Object::new, EXECUTOR.get() );
        CompletableFuture<String> future = FutureUtils.exceptionableFuture( () -> expectedValue, EXECUTOR.get() );

        CompletableFuture<String> failFastFuture =
                FutureUtils.failFastFuture( EXECUTOR.get(), future, exceptionable1, exceptionable2 );

        // when
        String result = failFastFuture.get();

        // then
        assertEquals( expectedValue, result );
    }

    @Test
    public void shouldReturnFutureThatFailsIfOneOfTheExceptionablesFailsBeforeFutureCompletes() throws
            ExecutionException,
            InterruptedException
    {
        // given
        IOException expectedException = new IOException( "IO error" );

        CompletableFuture<Object> completesSecondWithException = FutureUtils.exceptionableFuture( () -> {
            Thread.sleep( 100 );
            throw expectedException;
        }, EXECUTOR.get() );
        CompletableFuture<Object> completesFirst = FutureUtils.exceptionableFuture( () -> {
            Thread.sleep( 10 );
            return new Object();
        }, EXECUTOR.get() );

        CompletableFuture<String> completesLast = FutureUtils.exceptionableFuture( () -> {
            Thread.sleep( 200 );
            return "result";
        }, EXECUTOR.get() );

        CompletableFuture<String> failFastFuture = FutureUtils.failFastFuture(
                EXECUTOR.get(),
                completesLast,
                completesSecondWithException,
                completesFirst );

        try
        {
            // when
            failFastFuture.get();
            fail( "Expected ExecutionException" );
        }
        catch ( Exception e )
        {
            // then
            assertEquals( expectedException, e.getCause().getCause() );
        }
    }

    @Test
    public void shouldReturnFutureThatReturnsFutureValueIfOneOfTheExceptionablesFailsAfterFutureCompletes() throws
            ExecutionException,
            InterruptedException
    {
        // given
        String expectedValue = "result";

        CompletableFuture<Object> exceptionable1 = FutureUtils.exceptionableFuture( () -> {
            Thread.sleep( 2000 );
            throw new IOException( "IO error" );
        }, EXECUTOR.get() );
        CompletableFuture<Object> exceptionable2 = FutureUtils.exceptionableFuture( () -> {
            Thread.sleep( 100 );
            return new Object();
        }, EXECUTOR.get() );

        CompletableFuture<String> future = FutureUtils.exceptionableFuture( () -> {
            Thread.sleep( 10 );
            return expectedValue;
        }, EXECUTOR.get() );

        CompletableFuture<String> failFastFuture =
                FutureUtils.failFastFuture( EXECUTOR.get(), future, exceptionable1, exceptionable2 );

        // when
        String result = failFastFuture.get();

        // then
        assertEquals( expectedValue, result );
    }
}
