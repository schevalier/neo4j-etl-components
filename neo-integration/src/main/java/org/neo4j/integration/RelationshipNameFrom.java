package org.neo4j.integration;

public enum RelationshipNameFrom
{
    TABLE_NAME, COLUMN_NAME;

    public static RelationshipNameFrom parse( String relationshipNameFrom )
    {
        switch ( relationshipNameFrom )
        {
            case "table":
                return TABLE_NAME;

            case "column":
                return COLUMN_NAME;

            default:
                return TABLE_NAME;
        }
    }
}
