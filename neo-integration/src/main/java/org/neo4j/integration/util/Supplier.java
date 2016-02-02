package org.neo4j.integration.util;

public interface Supplier<T>
{
    T supply() throws Exception;
}
