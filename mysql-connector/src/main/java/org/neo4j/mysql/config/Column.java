package org.neo4j.mysql.config;

import org.neo4j.ingest.config.Field;

public class Column
{
    private final String name;
    private final Field field;

    public Column( String name, Field field )
    {
        this.name = name;
        this.field = field;
    }

    public String name()
    {
        return name;
    }

    public Field field()
    {
        return field;
    }
}
