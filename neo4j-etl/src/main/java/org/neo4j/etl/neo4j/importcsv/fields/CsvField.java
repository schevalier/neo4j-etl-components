package org.neo4j.etl.neo4j.importcsv.fields;

import com.fasterxml.jackson.databind.JsonNode;

import org.neo4j.etl.neo4j.importcsv.config.formatting.Formatter;

import static java.lang.String.format;

public interface CsvField
{
    static CsvField fromJson( JsonNode root )
    {
        String type = root.path( "type" ).textValue();

        if ( type.equalsIgnoreCase( StartId.class.getSimpleName() ) )
        {
            return StartId.fromJson( root );
        }
        else if ( type.equalsIgnoreCase( EndId.class.getSimpleName() ) )
        {
            return EndId.fromJson( root );
        }
        else if ( type.equalsIgnoreCase( RelationshipType.class.getSimpleName() ) )
        {
            return new RelationshipType();
        }
        else if ( type.equalsIgnoreCase( Id.class.getSimpleName() ) )
        {
            return Id.fromJson( root );
        }
        else if ( type.equalsIgnoreCase( Label.class.getSimpleName() ) )
        {
            return new Label();
        }
        else if ( type.equalsIgnoreCase( Data.class.getSimpleName() ) )
        {
            return Data.fromJson( root );
        }
        else
        {
            throw new IllegalStateException( format( "Unrecognized CsvField type: '%s'", type ) );
        }
    }

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

    static CsvField data( String name, Neo4jDataType type )
    {
        return new Data( name, type );
    }

    static CsvField array( String name, Neo4jDataType type )
    {
        return new Data( name, type, true );
    }

    String value( Formatter formatter );

    JsonNode toJson();
}
