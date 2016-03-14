package org.neo4j.integration.sql.metadata;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals( Object o )
    {
        return EqualsBuilder.reflectionEquals( this, o );
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode( this );
    }
}
