package org.neo4j.ingest.config;

class RelationshipType implements CsvFieldType
{
    @Override
    public void validate( boolean fieldIsNamed )
    {
        // Do nothing
    }

    @Override
    public String value()
    {
        return ":TYPE";
    }
}
