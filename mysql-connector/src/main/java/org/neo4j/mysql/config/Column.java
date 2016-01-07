package org.neo4j.mysql.config;

import org.neo4j.ingest.config.Field;
import org.neo4j.utils.Preconditions;

public class Column
{
    public static Builder.SetName builder()
    {
        return new ColumnBuilder();
    }

    private final String name;
    private final Field field;

    Column( ColumnBuilder builder )
    {
        this.name = Preconditions.requireNonNullString( builder.name, "Name" );
        this.field = Preconditions.requireNonNull( builder.field, "Field" );
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
        interface SetName
        {
            SetField name( String name );
        }

        interface SetField
        {
            Builder mapsTo( Field field );
        }

        Column build();
    }
}
