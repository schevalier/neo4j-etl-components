package org.neo4j.ingest.config;

class Label implements CsvFieldType
{
    Label()
    {
    }

    @Override
    public void validate( boolean fieldHasName )
    {
        // Do nothing
    }

    @Override
    public String value()
    {
        return ":LABEL";
    }
}
