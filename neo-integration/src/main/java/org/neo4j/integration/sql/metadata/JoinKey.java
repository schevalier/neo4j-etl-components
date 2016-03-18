package org.neo4j.integration.sql.metadata;

public class JoinKey
{
    private final Column sourceColumn;
    private final Column targetColumn;

    public JoinKey( Column sourceColumn, Column targetColumn )
    {
        this.sourceColumn = sourceColumn;
        this.targetColumn = targetColumn;
    }

    public Column sourceColumn()
    {
        return sourceColumn;
    }

    public Column targetColumn()
    {
        return targetColumn;
    }
}
