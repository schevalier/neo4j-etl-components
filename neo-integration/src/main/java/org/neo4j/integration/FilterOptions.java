package org.neo4j.integration;

import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;

public class FilterOptions
{
    public final TinyIntAs tinyIntAs;
    public final RelationshipNameFrom relationshipNameFrom;

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

    public enum RelationshipNameFrom
    {
        TABLE_NAME, COLUMN_NAME;

        public static RelationshipNameFrom parse( String relationshipNameFrom )
        {
            switch ( relationshipNameFrom )
            {
                case "table":
                    return RelationshipNameFrom.TABLE_NAME;

                case "column":
                    return RelationshipNameFrom.COLUMN_NAME;

                default:
                    return RelationshipNameFrom.TABLE_NAME;
            }
        }
    }

    public FilterOptions()
    {
        this.tinyIntAs = TinyIntAs.BYTE;
        this.relationshipNameFrom = RelationshipNameFrom.TABLE_NAME;
    }

    public FilterOptions( TinyIntAs tinyIntAs, RelationshipNameFrom relationshipNameFrom )
    {
        this.tinyIntAs = tinyIntAs;
        this.relationshipNameFrom = relationshipNameFrom;
    }

    public TinyIntAs getTinyIntAs()
    {
        return tinyIntAs;
    }

    public RelationshipNameFrom getRelationshipNameFrom()
    {
        return relationshipNameFrom;
    }
}
