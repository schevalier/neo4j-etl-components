package org.neo4j.integration.neo4j.importcsv.fields;

import java.io.Writer;

import org.neo4j.integration.neo4j.importcsv.config.QuoteChar;

public enum Neo4jDataType
{
    Boolean( false ),
    Int( false ),
    Long( false ),
    Float( false ),
    Double( false ),
    Byte( false ),
    Short( false ),
    Char( true ),
    String( true );

    private boolean useQuotes;

    Neo4jDataType( boolean useQuotes )
    {
        this.useQuotes = useQuotes;
    }

    public String value()
    {
        return name().toLowerCase();
    }

    @Override
    public String toString()
    {
        return value();
    }

    public boolean shouldUseQuotes()
    {
        return useQuotes;
    }
}
