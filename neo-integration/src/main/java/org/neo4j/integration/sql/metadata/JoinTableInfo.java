package org.neo4j.integration.sql.metadata;

public class JoinTableInfo
{
    private final TableName joinTableName;
    private final TableNamePair referencedTables;

    public JoinTableInfo( TableName joinTableName, TableNamePair referencedTables )
    {
        this.joinTableName = joinTableName;
        this.referencedTables = referencedTables;
    }

    public TableName joinTableName()
    {
        return joinTableName;
    }

    public TableNamePair referencedTables()
    {
        return referencedTables;
    }
}
