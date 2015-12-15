package org.neo4j.utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class FutureUtils
{
    // Returns a future that either completes successfully or fails as soon as one of the exceptionable futures
    // throws an exception.
    public static <T> CompletableFuture<T> failFastFuture( CompletableFuture<T> future,
                                                           CompletableFuture<?>... exceptionables )
    {
        CompletableFuture<T> result = new CompletableFuture<>();

        ForkJoinPool.commonPool().submit( () -> {
            try
            {
                CompletableFuture.anyOf( ArrayUtils.prepend( future, exceptionables ) ).get();
                result.complete( future.get() );
            }
            catch ( Exception ex )
            {
                result.completeExceptionally( ex );
            }
        } );

        return result;
    }

    // Returns a future that either completes successfully or throws an exception.
    public static <T> CompletableFuture<T> exceptionableFuture( Supplier<T> supplier )
    {
        CompletableFuture<T> future = new CompletableFuture<>();

        ForkJoinPool.commonPool().submit( () -> {
            try
            {
                future.complete( supplier.supply() );
            }
            catch ( Exception ex )
            {
                future.completeExceptionally( ex );
            }
        } );

        return future;
    }
}
