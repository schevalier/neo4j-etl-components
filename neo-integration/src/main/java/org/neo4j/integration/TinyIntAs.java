package org.neo4j.integration;

import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;

public enum TinyIntAs
{
    BYTE( Neo4jDataType.Byte ), BOOLEAN( Neo4jDataType.Boolean );

    private Neo4jDataType tinyIntAsNeoDataType;

    TinyIntAs( Neo4jDataType tinyIntAs )
    {
        this.tinyIntAsNeoDataType = tinyIntAs;
    }

    public Neo4jDataType neoDataType()
    {
        return tinyIntAsNeoDataType;
    }

    public static TinyIntAs parse( String tinyIntAs )
    {
        switch ( tinyIntAs )
        {
            case "byte":
                return TinyIntAs.BYTE;

            case "boolean":
                return TinyIntAs.BOOLEAN;

            default:
                return TinyIntAs.BYTE;
        }
    }
}
