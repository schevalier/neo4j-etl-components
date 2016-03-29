package org.neo4j.integration.neo4j.importcsv.fields;

import java.util.Optional;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.neo4j.integration.neo4j.importcsv.config.DefaultPropertyFormatter;
import org.neo4j.integration.neo4j.importcsv.config.Formatter;

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
    public String value( Formatter formatter )
    {
        return idSpace.isPresent() ? format( ":END_ID(%s)", idSpace.get().value() ) : ":END_ID";
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
        return HashCodeBuilder.reflectionHashCode( this );
    }
}
