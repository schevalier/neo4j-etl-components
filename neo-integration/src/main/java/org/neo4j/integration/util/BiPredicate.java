package org.neo4j.integration.util;

public interface BiPredicate<T, U>
{
    boolean test( T t, U u ) throws Exception;
}
