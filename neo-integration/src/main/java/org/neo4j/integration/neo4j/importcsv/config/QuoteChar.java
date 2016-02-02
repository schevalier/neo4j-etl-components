package org.neo4j.integration.neo4j.importcsv.config;

import org.neo4j.integration.util.OperatingSystem;

import static java.lang.String.format;

public class QuoteChar
{
    public static final QuoteChar DOUBLE_QUOTES = new QuoteChar( "\"", OperatingSystem.isWindows() ? "\\\"" : "\"" );
    public static final QuoteChar SINGLE_QUOTES = new QuoteChar( "'", "'" );

    private final String quote;
    private final String argValue;

    QuoteChar( String quote, String argValue )
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
        return format ("%s%s%s", quote, value, quote);
    }
}
