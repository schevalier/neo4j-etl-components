package org.neo4j.integration.sql.metadata;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static java.lang.String.format;

public class TableNamePair implements JoinQueryInfo
{
    private final TableName startTable;
    private final TableName endTable;

    public TableNamePair( TableName startTable, TableName endTable )
    {
        this.startTable = startTable;
        this.endTable = endTable;
    }

    @Override
    public TableName startTable()
    {
        return startTable;
    }

    @Override
    public TableName endTable()
    {
        return endTable;
    }

    @Override
    public TableName table()
    {
        return startTable;
    }

    @Override
    public String specialisedSql()
    {
        return format( "((source_column.COLUMN_KEY = 'PRI' AND join_table.REFERENCED_TABLE_NAME IS NULL) OR " +
                "  (join_table.REFERENCED_TABLE_NAME IN ('%s')))", endTable.simpleName() );
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
