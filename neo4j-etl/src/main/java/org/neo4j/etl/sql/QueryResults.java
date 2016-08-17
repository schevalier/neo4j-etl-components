package org.neo4j.etl.sql;

import java.util.Map;
import java.util.stream.Stream;

public interface QueryResults extends RowAccessor, AutoCloseable
{
    boolean next() throws Exception;

    Stream<Map<String, String>> stream();
}
