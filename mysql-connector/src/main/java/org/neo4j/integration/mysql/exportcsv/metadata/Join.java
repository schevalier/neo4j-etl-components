package org.neo4j.integration.mysql.exportcsv.metadata;

import java.util.Collection;

import org.neo4j.integration.util.Preconditions;

import static java.util.Arrays.asList;

public class Join
{
    public static Builder.SetParentTable builder()
    {
        return new JoinBuilder();
    }

    private final Column primaryKey;
    private final Column foreignKey;
    private final TableName childTable;

    Join( JoinBuilder builder )
    {
        this.primaryKey = Preconditions.requireNonNull( builder.primaryKey, "Primary key" );
        this.foreignKey = Preconditions.requireNonNull( builder.foreignKey, "Foreign key" );
        this.childTable = Preconditions.requireNonNull( builder.childTable, "Child table" );
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

    public interface Builder
    {
        interface SetParentTable
        {
            SetPrimaryKey parentTable(TableName parent);
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
            Builder childTable(TableName childTable);
        }

        Join build();
    }
}
