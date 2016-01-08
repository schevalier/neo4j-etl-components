package org.neo4j.ingest.config;

import java.util.Optional;

import static java.lang.String.format;

public class Field
{
    public static Field id()
    {
        return new Field( new Id() );
    }

    public static Field id( String name )
    {
        return new Field( name, new Id() );
    }

    public static Field id( IdSpace idSpace )
    {
        return new Field( new Id( idSpace ) );
    }

    public static Field id( String name, IdSpace idSpace )
    {
        return new Field( name, new Id( idSpace ) );
    }

    public static Field label()
    {
        return new Field( new Label() );
    }

    public static Field data( String name )
    {
        return new Field( name, new Data( DataType.String ) );
    }

    public static Field data( String name, DataType type )
    {
        return new Field( name, new Data( type ) );
    }

    public static Field array( String name, DataType type )
    {
        return new Field( name, new Data( type, true ) );
    }

    private final Optional<String> name;
    private final FieldType type;

    Field( FieldType type )
    {
        this( null, type );
    }

    Field( String name, FieldType type )
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
        return name.isPresent() ? format( "%s%s", name.get(), type.value() ) : type.value();
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
