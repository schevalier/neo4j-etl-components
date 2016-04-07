package org.neo4j.integration.neo4j.importcsv.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Delimiter
{
    public static final Delimiter TAB = new Delimiter( "\t", "TAB" );
    public static final Delimiter SEMICOLON = new Delimiter( ";" );
    public static final Delimiter COMMA = new Delimiter( "," );

    public static Delimiter fromJson( JsonNode root )
    {
        String value = root.path( "value" ).textValue();
        String description = root.path( "description" ).textValue();

        return new Delimiter( value, description );
    }

    private final String value;
    private final String description;

    public Delimiter( String value )
    {
        this( value, value );
    }

    public Delimiter( String value, String description )
    {
        this.value = value;
        this.description = description;
    }

    public String value()
    {
        return value;
    }

    public String description()
    {
        return description;
    }

    public JsonNode toJson()
    {
        ObjectNode root = JsonNodeFactory.instance.objectNode();

        root.put( "value", value );
        root.put( "description", description );

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
