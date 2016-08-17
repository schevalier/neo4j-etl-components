package org.neo4j.etl.neo4j.importcsv.fields;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.neo4j.etl.neo4j.importcsv.config.formatting.Formatter;
import org.neo4j.etl.util.Preconditions;

import static java.lang.String.format;

class Data implements CsvField
{
    static CsvField fromJson( JsonNode node )
    {
        String name = node.path( "name" ).textValue();
        String neo4jDataType = node.path( "neo4j-data-type" ).textValue();
        boolean isArray = node.path( "is-array" ).booleanValue();

        return new Data( name, Neo4jDataType.valueOf( neo4jDataType ), isArray );
    }

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
    public String value( Formatter formatter )
    {
        return isArray ?
                format( "%s:%s[]", formatter.format( name ), type.name().toLowerCase() ) :
                format( "%s:%s", formatter.format( name ), type.name().toLowerCase() );
    }

    @Override
    public JsonNode toJson()
    {
        ObjectNode root = JsonNodeFactory.instance.objectNode();

        root.put( "type", getClass().getSimpleName() );
        root.put( "name", name );
        root.put( "neo4j-data-type", type.name() );
        root.put( "is-array", isArray );

        return root;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals( Object o )
    {
        return EqualsBuilder.reflectionEquals( this, o );
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode( this );
    }
}
