package org.neo4j.integration.neo4j.importcsv.config;

public enum DataType
{
    Int,
    Long,
    Float,
    Double,
    Boolean,
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
