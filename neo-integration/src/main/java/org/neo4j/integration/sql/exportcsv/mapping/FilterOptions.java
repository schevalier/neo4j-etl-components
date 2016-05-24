package org.neo4j.integration.sql.exportcsv.mapping;

public class FilterOptions
{
    private final TinyIntAs tinyIntAs;
    private final RelationshipNameFrom relationshipNameFrom;

    public static FilterOptions DEFAULT = new FilterOptions( TinyIntAs.BYTE, RelationshipNameFrom.TABLE_NAME );

    public FilterOptions( String tinyIntAs, String relationshipNameFrom )
    {
        this( TinyIntAs.parse( tinyIntAs ), RelationshipNameFrom.parse( relationshipNameFrom ) );
    }

    private FilterOptions( TinyIntAs tinyIntAs, RelationshipNameFrom relationshipNameFrom )
    {
        this.tinyIntAs = tinyIntAs;
        this.relationshipNameFrom = relationshipNameFrom;
    }

    public TinyIntAs tinyIntAs()
    {
        return tinyIntAs;
    }

    public RelationshipNameFrom relationshipNameFrom()
    {
        return relationshipNameFrom;
    }
}
