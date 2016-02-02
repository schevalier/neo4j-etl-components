package org.neo4j.integration.neo4j.importcsv.config;

import static java.lang.String.format;

public class QuoteChar
{
    public static final QuoteChar DOUBLE_QUOTES = new QuoteChar( "\"" );
    public static final QuoteChar SINGLE_QUOTES = new QuoteChar( "'" );

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
