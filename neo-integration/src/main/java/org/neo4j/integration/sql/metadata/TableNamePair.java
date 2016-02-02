package org.neo4j.integration.sql.metadata;

public class TableNamePair
{
    private final TableName table1;
    private final TableName table2;

    public TableNamePair( TableName table1, TableName table2 )
    {
        this.table1 = table1;
        this.table2 = table2;
    }

    public TableName table1()
    {
        return table1;
    }

    public TableName table2()
    {
        return table2;
    }
}
