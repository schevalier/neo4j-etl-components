package org.neo4j.integration.io;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface AwaitHandle<T>
{
    static <T> AwaitHandle<T> noOp()
    {
        return new AwaitHandle<T>()
        {
            @Override
            public T await() throws Exception
            {
                return null;
            }

            @Override
            public T await( long timeout, TimeUnit unit ) throws Exception
            {
                return null;
            }

            @Override
            public CompletableFuture<T> toFuture()
            {
                return CompletableFuture.completedFuture( null );
            }
        };
    }

    static <T> AwaitHandle<T> forReturnValue( T returnValue )
    {
        return new AwaitHandle<T>()
        {
            @Override
            public T await() throws Exception
            {
                return returnValue;
            }

            @Override
            public T await( long timeout, TimeUnit unit ) throws Exception
            {
                return returnValue;
            }

            @Override
            public CompletableFuture<T> toFuture()
            {
                return CompletableFuture.completedFuture( returnValue );
            }
        };
    }

    T await() throws Exception;

    T await( long timeout, TimeUnit unit ) throws Exception;

    CompletableFuture<T> toFuture();
}
