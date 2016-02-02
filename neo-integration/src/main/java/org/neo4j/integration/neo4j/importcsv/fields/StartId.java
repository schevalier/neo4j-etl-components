package org.neo4j.integration.neo4j.importcsv.fields;

import java.util.Optional;

import static java.lang.String.format;

class StartId implements CsvField
{
    private final Optional<IdSpace> idSpace;

    StartId()
    {
        this( null );
    }

    StartId( IdSpace idSpace )
    {
        this.idSpace = Optional.ofNullable( idSpace );
    }

    @Override
    public String value()
    {
        return idSpace.isPresent() ? format( ":START_ID(%s)", idSpace.get().value() ) : ":START_ID";
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

        StartId startId = (StartId) o;

        return idSpace.equals( startId.idSpace );

    }

    @Override
    public int hashCode()
    {
        return idSpace.hashCode();
    }
}
