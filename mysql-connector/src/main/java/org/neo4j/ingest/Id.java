package org.neo4j.ingest;

import java.util.Optional;

public class Id implements FieldType
{
    public static Id id( IdType type )
    {
        return new Id( type, null );
    }

    public static Id id( IdType type, String idSpace )
    {
        return new Id( type, idSpace );
    }

    private final IdType type;
    private final Optional<String> idSpace;

    Id( IdType type, String idSpace )
    {
        this.type = type;
        this.idSpace = Optional.ofNullable( idSpace );
    }

    @Override
    public void validate( boolean fieldHasName )
    {
        // Do nothing
    }
}
