package org.neo4j.integration.sql.metadata;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import org.neo4j.integration.util.Preconditions;

public class Column
{
    private final TableName table;
    private final String name;
    private final String alias;
    private final ColumnType columnType;
    private final SqlDataType dataType;

    public Column( TableName table, String name, String alias, ColumnType columnType, SqlDataType dataType )
    {
        this.table = Preconditions.requireNonNull( table, "Table" );
        this.name = Preconditions.requireNonNullString( name, "Name" );
        this.alias = Preconditions.requireNonNullString( alias, "Alias" );
        this.columnType = Preconditions.requireNonNull( columnType, "ColumnType" );
        this.dataType = Preconditions.requireNonNull( dataType, "DataType" );
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
}
