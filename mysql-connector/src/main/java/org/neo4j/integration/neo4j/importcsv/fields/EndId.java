package org.neo4j.integration.neo4j.importcsv.fields;

import java.util.Optional;

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
    public String value()
    {
        return idSpace.isPresent() ? format( ":END_ID(%s)", idSpace.get().value() ) : ":END_ID";
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

        EndId endId = (EndId) o;

        return idSpace.equals( endId.idSpace );

    }

    @Override
    public int hashCode()
    {
        return idSpace.hashCode();
    }
}
