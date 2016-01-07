package org.neo4j.ingest.config;

import java.util.Optional;

import static java.lang.String.format;

public class Id implements FieldType
{
    public static final Id ID = new Id( null );

    public static Id id( String idSpace )
    {
        return new Id( idSpace );
    }

    private final Optional<String> idSpace;

    Id( String idSpace )
    {
        this.idSpace = Optional.ofNullable( idSpace );
    }

    @Override
    public void validate( boolean fieldHasName )
    {
        // Do nothing
    }

    @Override
    public String value()
    {
        return idSpace.isPresent() ? format( ":ID(%s)", idSpace.get() ) : ":ID";
    }
}
