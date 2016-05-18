package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.FilterOptions;

public class RelationshipNameResolver
{
    private FilterOptions.RelationshipNameFrom relationshipNameFrom;

    public RelationshipNameResolver( FilterOptions.RelationshipNameFrom relationshipNameFrom )
    {
        this.relationshipNameFrom = relationshipNameFrom;
    }

    String resolve( String tableName, String columnName )
    {
        switch ( relationshipNameFrom )
        {
            case TABLE_NAME:
                return tableName;

            case COLUMN_NAME:
                return columnName;

            default:
                return tableName;
        }
    }
}