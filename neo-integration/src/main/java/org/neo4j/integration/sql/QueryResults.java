package org.neo4j.integration.sql;

public interface QueryResults extends RowAccessor, AutoCloseable
{
    boolean next() throws Exception;
}
