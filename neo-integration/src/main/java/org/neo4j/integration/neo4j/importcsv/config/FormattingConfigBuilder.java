package org.neo4j.integration.neo4j.importcsv.config;

class FormattingConfigBuilder implements Formatting.Builder
{
    Delimiter delimiter = Delimiter.COMMA;
    Delimiter arrayDelimiter = Delimiter.SEMICOLON;
    QuoteChar quote = QuoteChar.DOUBLE_QUOTES;

    @Override
    public Formatting.Builder delimiter( Delimiter delimiter )
    {
        this.delimiter = delimiter;
        return this;
    }

    @Override
    public Formatting.Builder arrayDelimiter( Delimiter delimiter )
    {
        this.arrayDelimiter = delimiter;
        return this;
    }

    @Override
    public Formatting.Builder quote( QuoteChar quote )
    {
        this.quote = quote;
        return this;
    }

    @Override
    public Formatting build()
    {
        return new Formatting( this );
    }
}
