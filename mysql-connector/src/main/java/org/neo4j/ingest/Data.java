package org.neo4j.ingest;

import static java.lang.String.format;

public class Data implements FieldType
{
    public static Data ofType( DataType type )
    {
        return new Data( type, false );
    }

    public static Data arrayOfType( DataType type )
    {
        return new Data( type, true );
    }

    private final DataType type;
    private final boolean isArray;

    private Data( DataType type, boolean isArray )
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
    public String toString()
    {
        return format( "%s%s", isArray ? "array of " : "", type );
    }
}
