package org.neo4j.integration.sql.metadata;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static java.lang.String.format;

public class JoinTableInfo implements JoinQueryInfo
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
    public TableName tableOne()
    {
        return referencedTables.tableOne();
    }

    @Override
    public TableName tableTwo()
    {
        return referencedTables.tableTwo();
    }

    @Override
    public TableName table()
    {
        return joinTableName;
    }

    @Override
    public String specialisedSql()
    {
        return format( "join_table.REFERENCED_TABLE_NAME IN ('%s', '%s')",
                referencedTables.tableOne().simpleName(), referencedTables.tableTwo().simpleName() );
    }
}
