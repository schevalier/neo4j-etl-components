package org.neo4j.ingest.config;

import java.util.Optional;

import static java.lang.String.format;

class Id implements CsvFieldType
{
    private final Optional<IdSpace> idSpace;

    Id()
    {
        this( null );
    }

    Id( IdSpace idSpace )
    {
        this.idSpace = Optional.ofNullable( idSpace );
    }

    @Override
    public void validate( boolean fieldIsNamed )
    {
        // Do nothing
    }

    @Override
    public String value()
    {
        return idSpace.isPresent() ? format( ":ID(%s)", idSpace.get().value() ) : ":ID";
    }
}
