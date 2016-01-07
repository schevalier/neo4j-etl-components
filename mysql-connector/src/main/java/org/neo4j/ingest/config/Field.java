package org.neo4j.ingest.config;

import java.util.Optional;

import static java.lang.String.format;

public class Field
{
    private final Optional<String> name;
    private final FieldType type;

    public Field( FieldType type )
    {
        this( null, type );
    }

    public Field( String name, FieldType type )
    {
        this.name = Optional.ofNullable( orNull( name ) );
        this.type = type;
    }

    public void validate()
    {
        type.validate( name.isPresent() );
    }

    public String value()
    {
        return name.isPresent() ? format("%s%s", name.get(), type.value()) : type.value();
    }

    private String orNull( String value )
    {
        if ( value == null )
        {
            return null;
        }

        if ( value.trim().isEmpty() )
        {
            return null;
        }

        return value;
    }
}
