package org.neo4j.integration.sql.metadata;

import org.neo4j.integration.util.Preconditions;

public class Column
{
    public static Column.Builder.SetTable builder()
    {
        return new ColumnBuilder();
    }

    private final TableName table;
    private final String name;
    private final String alias;
    private final ColumnType type;

    Column( ColumnBuilder builder )
    {
        this.table = Preconditions.requireNonNull( builder.table, "Table" );
        this.name = Preconditions.requireNonNullString( builder.name, "Name" );
        this.alias = Preconditions.requireNonNullString( builder.alias, "Alias" );
        this.type = Preconditions.requireNonNull( builder.type, "Type" );
    }

    public TableName table()
    {
        return table;
    }

    // Fully-qualified column name, or literal value
    public String name()
    {
        return name;
    }

    // Column alias
    public String alias()
    {
        return alias;
    }

    public ColumnType type()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return "Column{" +
                "table=" + table +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        Column column = (Column) o;

        return table.equals( column.table ) &&
                name.equals( column.name ) &&
                alias.equals( column.alias ) &&
                type == column.type;

    }

    @Override
    public int hashCode()
    {
        int result = table.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + alias.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    public interface Builder
    {
        interface SetTable
        {
            SetName table( TableName table );
        }

        interface SetName
        {
            SetAlias name( String name );
        }

        interface SetAlias
        {
            SetType alias( String alias );
        }

        interface SetType
        {
            Builder type( ColumnType type );
        }

        Column build();
    }
}
