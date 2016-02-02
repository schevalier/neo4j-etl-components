package org.neo4j.integration.neo4j.importcsv.fields;

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

        RelationshipType that = (RelationshipType) o;

        return value.equals( that.value );

    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }
}
