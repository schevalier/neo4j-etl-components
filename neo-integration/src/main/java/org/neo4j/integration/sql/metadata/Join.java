package org.neo4j.integration.sql.metadata;

import java.util.Collection;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.neo4j.integration.util.Preconditions;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class Join extends DatabaseObject
{
    public static Builder.SetParentTable builder()
    {
        return new JoinBuilder();
    }

    private final Column primaryKey;
    private final Column foreignKey;
    private final TableName childTable;
    private final TableName startTable;

    Join( JoinBuilder builder )
    {
        this.primaryKey = Preconditions.requireNonNull( builder.primaryKey, "Primary key" );
        this.foreignKey = Preconditions.requireNonNull( builder.foreignKey, "Foreign key" );
        this.childTable = Preconditions.requireNonNull( builder.childTable, "Child table" );
        this.startTable = Preconditions.requireNonNull( builder.startTable, "Start table" );
    }

    public boolean childTableRepresentsStartOfRelationship()
    {
        return startTable.equals( childTable);
    }

    public boolean parentTableRepresentsStartOfRelationship()
    {
        return startTable.equals( primaryKey.table() );
    }

    public Column primaryKey()
    {
        return primaryKey;
    }

    public Column foreignKey()
    {
        return foreignKey;
    }

    public TableName childTable()
    {
        return childTable;
    }

    public Collection<TableName> tableNames()
    {
        return asList( primaryKey.table(), childTable );
    }

    @Override
    public String descriptor()
    {
        return format( "%s_%s", primaryKey.table().fullName(), childTable.fullName() );
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this );
    }

    public interface Builder
    {
        interface SetParentTable
        {
            SetPrimaryKey parentTable( TableName parent );
        }

        interface SetPrimaryKey
        {
            SetForeignKey primaryKey( String primaryKey );
        }

        interface SetForeignKey
        {
            SetChildTable foreignKey( String foreignKey );
        }

        interface SetChildTable
        {
            SetStartTable childTable( TableName childTable );
        }

        interface SetStartTable
        {
            Builder startTable( TableName startTable );
        }

        Join build();
    }
}
