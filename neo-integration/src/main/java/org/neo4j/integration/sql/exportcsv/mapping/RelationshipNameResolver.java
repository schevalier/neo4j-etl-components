package org.neo4j.integration.sql.exportcsv.mapping;

public class RelationshipNameResolver
{
    private boolean columnNameAsRelationshipLabel;

    public RelationshipNameResolver( boolean columnNameAsRelationshipLabel )
    {
        this.columnNameAsRelationshipLabel = columnNameAsRelationshipLabel;
    }

    String resolve( String tableName, String columnNameAsRelationship )
    {
        return columnNameAsRelationshipLabel ? columnNameAsRelationship : tableName;
    }
}