package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.ArrayList;
import java.util.List;

public class FilterOptions
{
    private final TinyIntAs tinyIntAs;
    private final RelationshipNameFrom relationshipNameFrom;

    private final List<String> tablesToExclude;

    public static FilterOptions DEFAULT = new FilterOptions( TinyIntAs.BYTE, RelationshipNameFrom.TABLE_NAME, new ArrayList<String>(  ) );

    public FilterOptions( String tinyIntAs, String relationshipNameFrom, List<String> tablesToExclude )
    {
        this( TinyIntAs.parse( tinyIntAs ), RelationshipNameFrom.parse( relationshipNameFrom ), tablesToExclude );
    }

    private FilterOptions( TinyIntAs tinyIntAs, RelationshipNameFrom relationshipNameFrom, List<String> tablesToExclude )
    {
        this.tinyIntAs = tinyIntAs;
        this.relationshipNameFrom = relationshipNameFrom;
        this.tablesToExclude = tablesToExclude;
    }

    public TinyIntAs tinyIntAs()
    {
        return tinyIntAs;
    }

    public RelationshipNameFrom relationshipNameFrom()
    {
        return relationshipNameFrom;
    }

    public List<String> tablesToExclude()
    {
        return tablesToExclude;
    }
}
