package org.neo4j.integration.util;

public interface Resource<T> extends AutoCloseable
{
    T get();
}
