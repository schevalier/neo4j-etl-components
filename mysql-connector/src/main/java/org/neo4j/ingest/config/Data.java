package org.neo4j.ingest.config;

import static java.lang.String.format;

class Data implements FieldType
{
    private final DataType type;
    private final boolean isArray;

    Data( DataType type )
    {
        this(type, false);
    }

    Data( DataType type, boolean isArray )
    {
        this.type = type;
        this.isArray = isArray;
    }

    @Override
    public void validate( boolean fieldHasName )
    {
        if ( !fieldHasName )
        {
            throw new IllegalStateException( format( "Name missing from field of type [%s]", this ) );
        }
    }

    @Override
    public String value()
    {
        return isArray ? format( ":%s[]", type.name().toLowerCase() ) : format( ":%s", type.name().toLowerCase() );
    }

    @Override
    public String toString()
    {
        return format( "%s%s", isArray ? "array of " : "", type );
    }
}
