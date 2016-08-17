package org.neo4j.etl.util;

public interface Supplier<T>
{
    T supply() throws Exception;
}
