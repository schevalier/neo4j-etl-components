package org.neo4j.ingest.config;

import java.util.Optional;

import org.neo4j.utils.Strings;

import static java.lang.String.format;

public class CsvField
{
    public static CsvField startId()
    {
        return new CsvField( new StartId() );
    }

    public static CsvField startId( IdSpace idSpace )
    {
        return new CsvField( new StartId( idSpace ) );
    }

    public static CsvField endId()
    {
        return new CsvField( new EndId() );
    }

    public static CsvField endId( IdSpace idSpace )
    {
        return new CsvField( new EndId( idSpace ) );
    }

    public static CsvField relationshipType()
    {
        return new CsvField( new RelationshipType() );
    }

    public static CsvField id()
    {
        return new CsvField( new Id() );
    }

    public static CsvField id( String name )
    {
        return new CsvField( name, new Id() );
    }

    public static CsvField id( IdSpace idSpace )
    {
        return new CsvField( new Id( idSpace ) );
    }

    public static CsvField id( String name, IdSpace idSpace )
    {
        return new CsvField( name, new Id( idSpace ) );
    }

    public static CsvField label()
    {
        return new CsvField( new Label() );
    }

    public static CsvField data( String name )
    {
        return new CsvField( name, new Data( DataType.String ) );
    }

    public static CsvField data( String name, DataType type )
    {
        return new CsvField( name, new Data( type ) );
    }

    public static CsvField array( String name, DataType type )
    {
        return new CsvField( name, new Data( type, true ) );
    }

    private final Optional<String> name;
    private final CsvFieldType type;

    CsvField( CsvFieldType type )
    {
        this( null, type );
    }

    CsvField( String name, CsvFieldType type )
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
