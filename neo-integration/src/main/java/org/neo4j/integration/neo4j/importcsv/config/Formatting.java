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

    Formatting( FormattingConfigBuilder builder )
    {
        this.delimiter = Preconditions.requireNonNull( builder.delimiter, "Delimiter" );
        this.arrayDelimiter = Preconditions.requireNonNull( builder.arrayDelimiter, "Array delimiter" );
        this.quote = Preconditions.requireNonNull( builder.quote, "Quote" );
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

    public interface Builder
    {
        Builder delimiter( Delimiter delimiter );

        Builder arrayDelimiter( Delimiter delimiter );

        Builder quote( String quote );

        Formatting build();
    }
}
