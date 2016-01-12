package org.neo4j.integration.io;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface AwaitHandle<T>
{
    T await() throws Exception;
    T await(long timeout, TimeUnit unit) throws Exception;
    CompletableFuture<T> toFuture();
}
