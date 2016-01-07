package org.neo4j.ingest.config;

import org.neo4j.utils.Preconditions;

public class Formatting
{
    public static final Formatting DEFAULT = builder().build();

    public static Builder builder()
    {
        return new FormattingConfigBuilder();
    }

    private final Delimiter delimiter;
    private final Delimiter arrayDelimiter;
    private final String quote;

    Formatting( FormattingConfigBuilder builder )
    {
        this.delimiter = Preconditions.requireNonNull( builder.delimiter, "Delimiter" );
        this.arrayDelimiter = Preconditions.requireNonNull( builder.arrayDelimiter, "Array delimiter" );
        this.quote = Preconditions.requireNonNullString( builder.quote, "Quote" );
    }

    public Delimiter delimiter()
    {
        return delimiter;
    }

    public Delimiter arrayDelimiter()
    {
        return arrayDelimiter;
    }

    public String quote()
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
