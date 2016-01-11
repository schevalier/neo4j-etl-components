package org.neo4j.ingest.config;

import java.util.Optional;

import static java.lang.String.format;

class StartId implements FieldType
{
    private final Optional<IdSpace> idSpace;

    StartId()
    {
        this( null );
    }

    StartId( IdSpace idSpace )
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
        return idSpace.isPresent() ? format( ":START_ID(%s)", idSpace.get().value() ) : ":START_ID";
    }
}
