package org.neo4j.integration.sql.metadata;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
        return ToStringBuilder.reflectionToString( this );
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
        return HashCodeBuilder.reflectionHashCode( 31 );
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
