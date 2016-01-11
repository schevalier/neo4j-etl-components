package org.neo4j.ingest.config;

import java.util.Optional;

import org.neo4j.utils.Strings;

import static java.lang.String.format;

public class Field
{
    public static Field startId()
    {
        return new Field( new StartId() );
    }

    public static Field startId( IdSpace idSpace )
    {
        return new Field( new StartId( idSpace ) );
    }

    public static Field endId()
    {
        return new Field( new EndId() );
    }

    public static Field endId( IdSpace idSpace )
    {
        return new Field( new EndId( idSpace ) );
    }

    public static Field relationshipType()
    {
        return new Field( new RelationshipType() );
    }

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
        this.name = Optional.ofNullable( Strings.orNull( name ) );
        this.type = type;

        validate();
    }

    public String value()
    {
        return name.isPresent() ? format( "%s%s", name.get(), type.value() ) : type.value();
    }

    private void validate()
    {
        type.validate( name.isPresent() );
    }
}
