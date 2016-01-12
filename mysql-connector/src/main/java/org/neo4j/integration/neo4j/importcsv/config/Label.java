package org.neo4j.integration.neo4j.importcsv.config;

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
