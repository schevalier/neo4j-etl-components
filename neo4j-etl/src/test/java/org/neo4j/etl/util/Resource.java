package org.neo4j.etl.util;

public interface Resource<T> extends AutoCloseable
{
    T get();
}
