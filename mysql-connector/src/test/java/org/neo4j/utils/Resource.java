package org.neo4j.utils;

public interface Resource<T> extends AutoCloseable
{
    T get();
}
