package org.neo4j.etl.sql;

public interface RowAccessor
{
    String getString( String columnLabel );
}
