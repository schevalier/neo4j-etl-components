package org.neo4j.ingest.config;

import static java.lang.String.format;

class Data implements CsvFieldType
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
    public void validate( boolean fieldIsNamed )
    {
        if ( !fieldIsNamed )
        {
            throw new IllegalStateException(
                    format( "Name missing from field of type [%s%s]", isArray ? "array of " : "", type ) );
        }
    }

    @Override
    public String value()
    {
        return isArray ? format( ":%s[]", type.name().toLowerCase() ) : format( ":%s", type.name().toLowerCase() );
    }
}
