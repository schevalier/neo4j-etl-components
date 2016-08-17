package org.neo4j.etl.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public class FutureUtils
{
    // Returns a future that either completes successfully or fails as soon as one of the exceptionable futures
    // throws an exception.
    public static <T> CompletableFuture<T> failFastFuture( CompletableFuture<T> future,
                                                           CompletableFuture<?>... exceptionables )
    {
        return failFastFuture( ForkJoinPool.commonPool(), future, exceptionables );
    }

    // Returns a future that either completes successfully or fails as soon as one of the exceptionable futures
    // throws an exception.
    public static <T> CompletableFuture<T> failFastFuture( Executor executor,
                                                           CompletableFuture<T> future,
                                                           CompletableFuture<?>... exceptionables )
    {
        CompletableFuture<T> result = new CompletableFuture<>();

        CompletableFuture.runAsync( () -> {

            while ( !result.isDone() )
            {
                if ( future.isDone() )
                {
                    try
                    {
                        result.complete( future.get() );
                    }
                    catch ( Exception e )
                    {
                        result.completeExceptionally( e );
                    }
                }
                else
                {
                    for ( CompletableFuture<?> exceptionable : exceptionables )
                    {
                        if ( exceptionable.isCompletedExceptionally() )
                        {
                            try
                            {
                                exceptionable.get();
                            }
                            catch ( Exception e )
                            {
                                result.completeExceptionally( e );
                                break;
                            }
                        }
                    }
                }
            }
        }, executor );

        return result;
    }

    // Returns a future that either completes successfully or throws an exception.
    public static <T> CompletableFuture<T> exceptionableFuture( Supplier<T> supplier )
    {
        return exceptionableFuture( supplier, ForkJoinPool.commonPool() );
    }

    // Returns a future that either completes successfully or throws an exception.
    public static <T> CompletableFuture<T> exceptionableFuture( Supplier<T> supplier, Executor executor )
    {
        CompletableFuture<T> future = new CompletableFuture<>();

        CompletableFuture.runAsync( () -> {
            try
            {
                future.complete( supplier.supply() );
            }
            catch ( Exception ex )
            {
                future.completeExceptionally( ex );
            }
        }, executor );

        return future;
    }
}
