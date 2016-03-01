package org.neo4j.integration.sql.metadata;

public class TableNamePair
{
    private final TableName startTable;
    private final TableName endTable;

    public TableNamePair( TableName startTable, TableName endTable )
    {
        this.startTable = startTable;
        this.endTable = endTable;
    }

    public TableName startTable()
    {
        return startTable;
    }

    public TableName endTable()
    {
        return endTable;
    }
}
