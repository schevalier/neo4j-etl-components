package org.neo4j.integration.neo4j.importcsv.fields;

public enum Neo4jDataType
{
    Boolean,
    Int,
    Long,
    Float,
    Double,
    Byte,
    Short,
    Char,
    String;

    public String value()
    {
        return name().toLowerCase();
    }

    @Override
    public String toString()
    {
        return value();
    }
}
