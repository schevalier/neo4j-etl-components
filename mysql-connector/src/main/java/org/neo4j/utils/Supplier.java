package org.neo4j.utils;

public interface Supplier<T>
{
    T supply() throws Exception;
}
