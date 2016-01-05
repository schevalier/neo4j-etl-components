package org.neo4j.ingest;

public class ImportConfig
{
    public static Builder builder()
    {
        return new ImportConfigBuilder();
    }

    private final String delimiter;
    private final String arrayDelimiter;
    private final String quote;

    ImportConfig( ImportConfigBuilder builder )
    {
        this.delimiter = builder.delimiter;
        this.arrayDelimiter = builder.arrayDelimiter;
        this.quote = builder.quote;
    }

    public String delimiter()
    {
        return delimiter;
    }

    public String arrayDelimiter()
    {
        return arrayDelimiter;
    }

    public String quote()
    {
        return quote;
    }

    public interface Builder
    {
        Builder delimiter( String delimiter );

        Builder arrayDelimiter( String delimiter );

        Builder quote(String quote);

        ImportConfig build();
    }
}
