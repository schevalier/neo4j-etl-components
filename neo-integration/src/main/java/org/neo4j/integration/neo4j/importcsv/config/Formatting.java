package org.neo4j.integration.neo4j.importcsv.config;

import org.neo4j.integration.util.Preconditions;

public class Formatting
{
    public static final Formatting DEFAULT = builder().build();

    public static Builder builder()
    {
        return new FormattingConfigBuilder();
    }

    private final Delimiter delimiter;
    private final Delimiter arrayDelimiter;
    private final QuoteChar quote;
    private final Formatter labelFormatter;
    private final Formatter relationshipFormatter;

    Formatting( FormattingConfigBuilder builder )
    {
        this.delimiter = Preconditions.requireNonNull( builder.delimiter, "Delimiter" );
        this.arrayDelimiter = Preconditions.requireNonNull( builder.arrayDelimiter, "ArrayDelimiter" );
        this.quote = Preconditions.requireNonNull( builder.quote, "Quote" );
        this.labelFormatter = Preconditions.requireNonNull( builder.labelFormatter, "LabelFormatter" );
        this.relationshipFormatter = Preconditions.requireNonNull( builder.relationshipFormatter,
                "RelationshipFormatter" );
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

    public interface Builder
    {
        Builder delimiter( Delimiter delimiter );

        Builder arrayDelimiter( Delimiter delimiter );

        Builder quote( QuoteChar quote );

        Builder labelFormatter( Formatter labelFormatter );

        Builder relationshipFormatter( Formatter relationshipFormatter );

        Formatting build();
    }
}
