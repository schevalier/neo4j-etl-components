package org.neo4j.ingest.config;

public class IdSpace
{
    private final String value;

    public IdSpace( String value )
    {
        this.value = value;
    }

    public String value()
    {
        return value;
    }
}
