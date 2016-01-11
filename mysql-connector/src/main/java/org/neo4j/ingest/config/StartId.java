package org.neo4j.ingest.config;

class StartId implements FieldType
{
    @Override
    public void validate( boolean fieldHasName )
    {
        // Do nothing
    }

    @Override
    public String value()
    {
        return ":START_ID";
    }
}
