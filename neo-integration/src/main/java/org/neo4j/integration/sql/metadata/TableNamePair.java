package org.neo4j.integration.sql.metadata;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static java.lang.String.format;

public class TableNamePair implements JoinQueryInfo
{
    private final TableName tableOne;
    private final TableName tableTwo;

    public TableNamePair( TableName tableOne, TableName tableTwo )
    {
        this.tableOne = tableOne;
        this.tableTwo = tableTwo;
    }

    @Override
    public TableName tableOne()
    {
        return tableOne;
    }

    @Override
    public TableName tableTwo()
    {
        return tableTwo;
    }

    @Override
    public TableName table()
    {
        return tableOne;
    }

    @Override
    public String specialisedSql()
    {
        return format( "((source_column.COLUMN_KEY = 'PRI' AND join_table.REFERENCED_TABLE_NAME IS NULL) OR " +
                "  (join_table.REFERENCED_TABLE_NAME IN ('%s')))", tableTwo.simpleName() );
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

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this );
    }
}
