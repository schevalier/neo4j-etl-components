package org.neo4j.integration.neo4j.importcsv.config;

class RelationshipType implements CsvField
{
    @Override
    public String value()
    {
        return ":TYPE";
    }
}
