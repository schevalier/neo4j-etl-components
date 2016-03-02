package org.neo4j.integration.sql.metadata;

public abstract class DatabaseObject
{
    public abstract String descriptor();

    public boolean isJoin()
    {
        return this instanceof Join;
    }

    public boolean isJoinTable()
    {
        return this instanceof JoinTable;
    }

    public boolean isTable()
    {
        return this instanceof Table;
    }
}
