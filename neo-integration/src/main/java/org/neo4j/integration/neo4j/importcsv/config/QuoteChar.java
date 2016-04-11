package org.neo4j.integration.neo4j.importcsv.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.neo4j.integration.util.OperatingSystem;

import static java.lang.String.format;

public class QuoteChar
{
    public static final QuoteChar DOUBLE_QUOTES = new QuoteChar( "\"", OperatingSystem.isWindows() ? "\\\"" : "\"" );
    public static final QuoteChar SINGLE_QUOTES = new QuoteChar( "'", "'" );
    public static final QuoteChar TICK_QUOTES = new QuoteChar( "`", "`" );

    public static QuoteChar fromJson( JsonNode root )
    {
        String quote = root.path( "quote" ).textValue();
        String argValue = root.path( "arg-value" ).textValue();

        return new QuoteChar( quote, argValue );
    }

    private final String quote;
    private final String argValue;

    public QuoteChar( String quote, String argValue )
    {
        this.quote = quote;
        this.argValue = argValue;
    }

    public String value()
    {
        return quote;
    }

    public String argValue()
    {
        return argValue;
    }

    public String enquote( String value )
    {
        return format( "%s%s%s", quote, value, quote );
    }

    public JsonNode toJson()
    {
        ObjectNode root = JsonNodeFactory.instance.objectNode();

        root.put( "quote", quote );
        root.put( "arg-value", argValue );

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
