package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.Collections;
import java.util.List;

public class FilterOptions
{
    private final TinyIntAs tinyIntAs;
    private final RelationshipNameFrom relationshipNameFrom;

    private final List<String> tablesToExclude;
    private final boolean excludeIncompleteJoinTables;

    public static FilterOptions DEFAULT = new FilterOptions(
            TinyIntAs.BYTE,
            RelationshipNameFrom.TABLE_NAME,
            Collections.EMPTY_LIST,
            false );

    public FilterOptions( String tinyIntAs,
                          String relationshipNameFrom,
                          List<String> tablesToExclude,
                          boolean excludeIncompleteJoinTables )
    {
        this(
                TinyIntAs.parse( tinyIntAs ),
                RelationshipNameFrom.parse( relationshipNameFrom ),
                tablesToExclude,
                excludeIncompleteJoinTables );
    }

    private FilterOptions( TinyIntAs tinyIntAs,
                           RelationshipNameFrom relationshipNameFrom,
                           List<String> tablesToExclude,
                           boolean excludeIncompleteJoinTables )
    {
        this.tinyIntAs = tinyIntAs;
        this.relationshipNameFrom = relationshipNameFrom;

        this.tablesToExclude = tablesToExclude;
        this.excludeIncompleteJoinTables = excludeIncompleteJoinTables;
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

    public boolean excludeIncompleteJoinTables()
    {
        return excludeIncompleteJoinTables;
    }
}
