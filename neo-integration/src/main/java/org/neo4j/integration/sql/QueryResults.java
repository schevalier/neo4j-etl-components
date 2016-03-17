package org.neo4j.integration.sql;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface QueryResults extends RowAccessor, AutoCloseable
{
    boolean next() throws Exception;

    Stream<Map<String, String>> streamOfResults( List<String> columnLabels );
}
