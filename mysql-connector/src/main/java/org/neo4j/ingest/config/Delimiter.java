package org.neo4j.ingest.config;

public class Delimiter
{
    public static final Delimiter TAB = new Delimiter( "\\t", "TAB" );
    public static final Delimiter SEMICOLON = new Delimiter( ";" );
    public static final Delimiter COMMA = new Delimiter( "," );

    private final String value;
    private final String description;

    public Delimiter(String value)
    {
        this(value, value);
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
}
