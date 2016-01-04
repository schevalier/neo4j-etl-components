package org.neo4j.io;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.neo4j.command_line.Result;

public interface AwaitHandle<T>
{
    T await() throws Exception;
    T await(long timeout, TimeUnit unit) throws Exception;
    CompletableFuture<T> toFuture();
}
