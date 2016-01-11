package org.neo4j.ingest.config;

class EndId implements FieldType
{
    @Override
    public void validate( boolean fieldHasName )
    {
        // Do nothing
    }

    @Override
    public String value()
    {
        return ":END_ID";
    }
}
