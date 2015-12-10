package org.neo4j.mysql;

public interface Opener<T>
{
    T open() throws Exception;
}
