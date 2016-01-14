package org.neo4j.integration.neo4j.importcsv.fields;

class Label implements CsvField
{
    private final String value = ":LABEL";

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
