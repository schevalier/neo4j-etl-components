package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.RelationshipNameFrom;

public class RelationshipNameResolver
{
    private RelationshipNameFrom relationshipNameFrom;

    public RelationshipNameResolver( RelationshipNameFrom relationshipNameFrom )
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
