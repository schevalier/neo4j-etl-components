package org.neo4j.mysql.config;

import java.util.Objects;

import org.neo4j.ingest.config.Field;
import org.neo4j.utils.Preconditions;

public class Column
{
    public static Builder withName(String name)
    {
        return new ColumnBuilder(name);
    }

    private final String name;
    private final Field field;

    Column(ColumnBuilder builder)
    {
        this.name = Preconditions.requireNonNullString( builder.name, "Name cannot be null or empty string" );
        this.field = Objects.requireNonNull(builder.field, "Field cannot be null");
    }

    public String name()
    {
        return name;
    }

    public Field field()
    {
        return field;
    }

    public interface Builder
    {
       Column mapsTo(Field field);
    }
}
