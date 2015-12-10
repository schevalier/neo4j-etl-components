package org.neo4j.mysql;

public interface PipeReader extends Exceptions, AutoCloseable
{
    void open() throws Exception;
}
