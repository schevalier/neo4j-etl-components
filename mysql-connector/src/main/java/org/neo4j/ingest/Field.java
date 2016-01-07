package org.neo4j.ingest;

import java.util.Optional;

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
