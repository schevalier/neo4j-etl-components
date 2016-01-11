package org.neo4j.ingest.config;

import java.util.Optional;

import static java.lang.String.format;

class EndId implements FieldType
{
    private final Optional<IdSpace> idSpace;

    EndId()
    {
        this( null );
    }

    EndId( IdSpace idSpace )
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
        return idSpace.isPresent() ? format( ":END_ID(%s)", idSpace.get().value() ) : ":END_ID";
    }
}
