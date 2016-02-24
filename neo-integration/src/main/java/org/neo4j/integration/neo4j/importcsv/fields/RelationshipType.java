package org.neo4j.integration.neo4j.importcsv.fields;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

class RelationshipType implements CsvField
{
    private final String value = ":TYPE";

    @Override
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
        return HashCodeBuilder.reflectionHashCode( 31 );
    }
}
