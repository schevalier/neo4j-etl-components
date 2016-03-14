package org.neo4j.integration.sql.metadata;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
