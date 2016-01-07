package org.neo4j.mysql;

import org.neo4j.ingest.Field;

public class Column
{
    private final String name;
    private final Field field;

    public Column( String name, Field field )
    {
        this.name = name;
        this.field = field;
    }
}
