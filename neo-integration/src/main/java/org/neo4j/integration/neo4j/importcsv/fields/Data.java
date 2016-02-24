package org.neo4j.integration.neo4j.importcsv.fields;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.neo4j.integration.util.Preconditions;

import static java.lang.String.format;

class Data implements CsvField
{
    private final String name;
    private final Neo4jDataType type;
    private final boolean isArray;

    Data( String name, Neo4jDataType type )
    {
        this( name, type, false );
    }

    Data( String name, Neo4jDataType type, boolean isArray )
    {
        this.name = Preconditions.requireNonNullString( name, "Name" );
        this.type = Preconditions.requireNonNull( type, "Type" );
        this.isArray = isArray;
    }

    @Override
    public String value()
    {
        return isArray ?
                format( "%s:%s[]", name, type.name().toLowerCase() ) :
                format( "%s:%s", name, type.name().toLowerCase() );
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
