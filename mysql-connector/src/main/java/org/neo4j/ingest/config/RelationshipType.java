package org.neo4j.ingest.config;

class RelationshipType implements CsvField
{
    @Override
    public String value()
    {
        return ":TYPE";
    }
}
