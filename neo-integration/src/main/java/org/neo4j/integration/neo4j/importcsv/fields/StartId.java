package org.neo4j.integration.neo4j.importcsv.fields;

import java.util.Optional;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public String toString()
    {
        return value();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals( Object o )
    {
        return EqualsBuilder.reflectionEquals( this, o );
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode( 31 );
    }
}
