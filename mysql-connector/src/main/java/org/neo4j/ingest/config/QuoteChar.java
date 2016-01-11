package org.neo4j.ingest.config;

import static java.lang.String.format;

public class QuoteChar
{
    private final String quote;

    QuoteChar( String quote )
    {
        this.quote = quote;
    }

    public String value()
    {
        return quote;
    }

    public String enquote( String value )
    {
        return format ("%s%s%s", quote, value, quote);
    }
}
