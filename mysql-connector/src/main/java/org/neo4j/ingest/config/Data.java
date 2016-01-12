package org.neo4j.ingest.config;

import org.neo4j.utils.Preconditions;

import static java.lang.String.format;

class Data implements CsvField
{
    private final String name;
    private final DataType type;
    private final boolean isArray;

    Data( String name, DataType type )
    {
        this(name, type, false);
    }

    Data( String name, DataType type, boolean isArray )
    {
        this.name = Preconditions.requireNonNullString( name, "Name" );
        this.type = type;
        this.isArray = isArray;
    }

    @Override
    public String value()
    {
        return isArray ?
                format( "%s:%s[]", name, type.name().toLowerCase() ) :
                format( "%s:%s", name, type.name().toLowerCase() );
    }
}
