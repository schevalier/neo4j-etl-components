package org.neo4j.integration.neo4j.importcsv.config;

import java.util.Optional;

import static java.lang.String.format;

class StartId implements CsvField
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
    public String value()
    {
        return idSpace.isPresent() ? format( ":START_ID(%s)", idSpace.get().value() ) : ":START_ID";
    }
}
