package org.neo4j.integration.sql;

public interface QueryResults extends AutoCloseable
{
    boolean next() throws Exception;

    String getString( String columnLabel ) throws Exception;
}
