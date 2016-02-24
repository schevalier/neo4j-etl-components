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
    private final ColumnType columnType;
    private final SqlDataType dataType;

    Column( ColumnBuilder builder )
    {
        this.table = Preconditions.requireNonNull( builder.table, "Table" );
        this.name = Preconditions.requireNonNullString( builder.name, "Name" );
        this.alias = Preconditions.requireNonNullString( builder.alias, "Alias" );
        this.columnType = Preconditions.requireNonNull( builder.columnType, "ColumnType" );
        this.dataType = Preconditions.requireNonNull( builder.dataType, "DataType" );
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
        return columnType;
    }

    public SqlDataType dataType()
    {
        return dataType;
    }

    @Override
    public String toString()
    {
        return "Column{" +
                "table=" + table +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", columnType=" + columnType +
                ", dataType='" + dataType + '\'' +
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

        return table.equals( column.table ) && name.equals( column.name ) && alias.equals( column.alias ) &&
                columnType == column.columnType && dataType.equals( column.dataType );

    }

    @Override
    public int hashCode()
    {
        int result = table.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + alias.hashCode();
        result = 31 * result + columnType.hashCode();
        result = 31 * result + dataType.hashCode();
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
            SetColumnType alias( String alias );
        }

        interface SetColumnType
        {
            SetDataType columnType( ColumnType columnType );
        }

        interface SetDataType
        {
            Builder dataType( SqlDataType dataType );
        }

        Column build();
    }
}
