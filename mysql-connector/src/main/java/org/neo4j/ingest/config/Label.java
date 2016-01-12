package org.neo4j.ingest.config;

class Label implements CsvField
{
    Label()
    {
    }

    @Override
    public String value()
    {
        return ":LABEL";
    }
}
