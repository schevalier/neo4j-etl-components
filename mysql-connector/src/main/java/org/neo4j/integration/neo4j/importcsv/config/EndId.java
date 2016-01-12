package org.neo4j.integration.neo4j.importcsv.config;

import java.util.Optional;

import static java.lang.String.format;

class EndId implements CsvField
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
    public String value()
    {
        return idSpace.isPresent() ? format( ":END_ID(%s)", idSpace.get().value() ) : ":END_ID";
    }
}
