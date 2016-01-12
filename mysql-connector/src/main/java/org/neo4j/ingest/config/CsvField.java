package org.neo4j.ingest.config;

public interface CsvField
{
    static CsvField startId()
    {
        return new StartId();
    }

    static CsvField startId( IdSpace idSpace )
    {
        return new StartId( idSpace );
    }

    static CsvField endId()
    {
        return new EndId();
    }

    static CsvField endId( IdSpace idSpace )
    {
        return new EndId( idSpace );
    }

    static CsvField relationshipType()
    {
        return new RelationshipType();
    }

    static CsvField id()
    {
        return new Id();
    }

    static CsvField id( String name )
    {
        return new Id( name );
    }

    static CsvField id( IdSpace idSpace )
    {
        return new Id( idSpace );
    }

    static CsvField id( String name, IdSpace idSpace )
    {
        return new Id( name, idSpace );
    }

    static CsvField label()
    {
        return new Label();
    }

    static CsvField data( String name )
    {
        return new Data( name, DataType.String );
    }

    static CsvField data( String name, DataType type )
    {
        return new Data( name, type );
    }

    static CsvField array( String name, DataType type )
    {
        return new Data( name, type, true );
    }

    String value();
}
