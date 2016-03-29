package org.neo4j.integration.neo4j.importcsv.fields;

import org.neo4j.integration.neo4j.importcsv.config.Formatter;

class Label implements CsvField
{
    private final String value = ":LABEL";

    @Override
    public String value( Formatter formatter )
    {
        return value;
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

        Label label = (Label) o;

        return value.equals( label.value );

    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }
}
