package org.neo4j.etl.neo4j.importcsv.fields;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.neo4j.etl.util.Preconditions;

public class IdSpace
{
    private final String value;

    public IdSpace( String value )
    {
        this.value = Preconditions.requireNonNullString( value, "Value" ).toLowerCase();
    }

    public String value()
    {
        return value;
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
        return HashCodeBuilder.reflectionHashCode( this );
    }
}
