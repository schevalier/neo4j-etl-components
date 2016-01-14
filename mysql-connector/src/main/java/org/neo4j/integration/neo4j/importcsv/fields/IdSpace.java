package org.neo4j.integration.neo4j.importcsv.fields;

import org.neo4j.integration.util.Preconditions;

public class IdSpace
{
    private final String value;

    public IdSpace( String value )
    {
        this.value = Preconditions.requireNonNullString( value, "Value" );
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

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        IdSpace idSpace = (IdSpace) o;

        return value.equals( idSpace.value );

    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }
}
