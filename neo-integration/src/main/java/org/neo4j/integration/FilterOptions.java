package org.neo4j.integration;

public class FilterOptions
{
    public TinyIntAs tinyIntAs;
    public RelationshipNameFrom relationshipNameFrom;

    public enum TinyIntAs
    {
        BYTE, BOOLEAN;
    }

    public enum RelationshipNameFrom
    {
        TABLE_NAME, COLUMN_NAME;
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

    public void setTinyIntAs( TinyIntAs tinyIntAs )
    {
        this.tinyIntAs = tinyIntAs;
    }

    public void setTinyIntAs( String tinyIntAs )
    {
        switch ( tinyIntAs )
        {
            case "byte":
                setTinyIntAs( TinyIntAs.BYTE );
                break;

            case "boolean":
                setTinyIntAs( TinyIntAs.BOOLEAN );
                break;

            default:
                setTinyIntAs( TinyIntAs.BYTE );
                break;
        }
    }

    public RelationshipNameFrom getRelationshipNameFrom()
    {
        return relationshipNameFrom;
    }

    public void setRelationshipNameFrom( RelationshipNameFrom relationshipNameFrom )
    {
        this.relationshipNameFrom = relationshipNameFrom;
    }

    public void setRelationshipNameFrom( String relationshipNameFrom )
    {
        switch ( relationshipNameFrom )
        {
            case "table":
                setRelationshipNameFrom( RelationshipNameFrom.TABLE_NAME );
                break;

            case "column":
                setRelationshipNameFrom( RelationshipNameFrom.COLUMN_NAME );
                break;

            default:
                setRelationshipNameFrom( RelationshipNameFrom.TABLE_NAME );
                break;
        }
    }
}
