package org.neo4j.integration.neo4j.importcsv.fields;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.neo4j.integration.neo4j.importcsv.config.DefaultPropertyFormatter;
import org.neo4j.integration.neo4j.importcsv.config.Formatter;

class RelationshipType implements CsvField
{
    private static final String value = ":TYPE";

    @Override
    public String value( Formatter formatter )
    {
        return value;
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
