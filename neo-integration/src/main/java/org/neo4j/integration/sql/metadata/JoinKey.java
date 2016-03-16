package org.neo4j.integration.sql.metadata;

public class JoinKey
{
    private final Column source;
    private final Column target;

    public JoinKey( Column source, Column target )
    {
        this.source = source;
        this.target = target;
    }

    public Column source()
    {
        return source;
    }

    public Column target()
    {
        return target;
    }
}
