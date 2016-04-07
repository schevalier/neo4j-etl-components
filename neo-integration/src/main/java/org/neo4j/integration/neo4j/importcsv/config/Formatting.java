package org.neo4j.integration.neo4j.importcsv.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.neo4j.integration.util.Preconditions;

import static java.lang.String.format;

public class Formatting
{
    public static final Formatting DEFAULT = builder().build();

    public static Builder builder()
    {
        return new FormattingConfigBuilder();
    }

    public static Formatting fromJson( JsonNode root )
    {
        Delimiter delimiter = Delimiter.fromJson( root.path( "delimiter" ) );
        Delimiter arrayDelimiter = Delimiter.fromJson( root.path( "array-delimiter" ) );
        QuoteChar quote = QuoteChar.fromJson( root.path( "quote" ) );

        Formatter labelFormatter = createFormatter( root.path( "label-formatter" ).textValue() );
        Formatter relationshipFormatter = createFormatter( root.path( "relationship-formatter" ).textValue() );
        Formatter propertyFormatter = createFormatter( root.path( "property-formatter" ).textValue() );

        return builder()
                .delimiter( delimiter )
                .arrayDelimiter( arrayDelimiter )
                .quote( quote )
                .labelFormatter( labelFormatter )
                .relationshipFormatter( relationshipFormatter )
                .propertyFormatter( propertyFormatter )
                .build();
    }

    private static Formatter createFormatter( String className )
    {
        try
        {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            Class<?> formatterClass = classLoader.loadClass( className );
            return (Formatter) formatterClass.newInstance();
        }
        catch ( ClassNotFoundException | IllegalAccessException | InstantiationException e )
        {
            throw new IllegalStateException( format( "Unable to create formatter '%s'", className ), e );
        }
    }

    private final Delimiter delimiter;
    private final Delimiter arrayDelimiter;
    private final QuoteChar quote;
    private final Formatter labelFormatter;
    private final Formatter relationshipFormatter;
    private final Formatter propertyFormatter;

    Formatting( FormattingConfigBuilder builder )
    {
        this.delimiter = Preconditions.requireNonNull( builder.delimiter, "Delimiter" );
        this.arrayDelimiter = Preconditions.requireNonNull( builder.arrayDelimiter, "ArrayDelimiter" );
        this.quote = Preconditions.requireNonNull( builder.quote, "Quote" );
        this.labelFormatter = Preconditions.requireNonNull( builder.labelFormatter, "LabelFormatter" );
        this.relationshipFormatter = Preconditions.requireNonNull( builder.relationshipFormatter,
                "RelationshipFormatter" );
        this.propertyFormatter = Preconditions.requireNonNull( builder.propertyFormatter,
                "PropertyFormatter" );
    }

    public Delimiter delimiter()
    {
        return delimiter;
    }

    public Delimiter arrayDelimiter()
    {
        return arrayDelimiter;
    }

    public QuoteChar quote()
    {
        return quote;
    }

    public Formatter labelFormatter()
    {
        return labelFormatter;
    }

    public Formatter relationshipFormatter()
    {
        return relationshipFormatter;
    }

    public Formatter propertyFormatter()
    {
        return propertyFormatter;
    }

    public JsonNode toJson()
    {
        ObjectNode root = JsonNodeFactory.instance.objectNode();

        root.set( "delimiter", delimiter.toJson() );
        root.set( "array-delimiter", arrayDelimiter.toJson() );
        root.set( "quote", quote.toJson() );
        root.put( "label-formatter", labelFormatter.getClass().getName() );
        root.put( "relationship-formatter", relationshipFormatter.getClass().getName() );
        root.put( "property-formatter", propertyFormatter.getClass().getName() );

        return root;
    }

    public interface Builder
    {
        Builder delimiter( Delimiter delimiter );

        Builder arrayDelimiter( Delimiter delimiter );

        Builder quote( QuoteChar quote );

        Builder labelFormatter( Formatter labelFormatter );

        Builder relationshipFormatter( Formatter relationshipFormatter );

        Builder propertyFormatter( Formatter propertyFormatter );

        Formatting build();
    }
}
