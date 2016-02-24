package org.neo4j.integration.neo4j.importcsv.fields;

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

        Data data = (Data) o;

        return isArray == data.isArray && name.equals( data.name ) && type == data.type;
    }

    @Override
    public int hashCode()
    {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (isArray ? 1 : 0);
        return result;
    }
}
