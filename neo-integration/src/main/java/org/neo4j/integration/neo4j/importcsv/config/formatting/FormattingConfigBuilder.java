package org.neo4j.integration.neo4j.importcsv.config.formatting;

class FormattingConfigBuilder implements Formatting.Builder
{
    Delimiter delimiter = Delimiter.COMMA;
    Delimiter arrayDelimiter = Delimiter.SEMICOLON;
    QuoteChar quote = QuoteChar.DOUBLE_QUOTES;
    Formatter labelFormatter = new DefaultLabelFormatter();
    Formatter relationshipFormatter = new DefaultRelationshipFormatter();
    Formatter propertyFormatter = new DefaultPropertyFormatter();

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
    public Formatting.Builder labelFormatter( Formatter labelFormatter )
    {
        this.labelFormatter = labelFormatter;
        return this;
    }

    @Override
    public Formatting.Builder relationshipFormatter( Formatter relationshipFormatter )
    {
        this.relationshipFormatter = relationshipFormatter;
        return this;
    }

    @Override
    public Formatting.Builder propertyFormatter( Formatter propertyFormatter )
    {
        this.propertyFormatter = propertyFormatter;
        return this;
    }

    @Override
    public Formatting build()
    {
        return new Formatting( this );
    }
}
