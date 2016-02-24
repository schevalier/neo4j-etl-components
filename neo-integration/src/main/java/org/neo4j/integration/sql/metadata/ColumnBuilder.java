package org.neo4j.integration.sql.metadata;

class ColumnBuilder implements Column.Builder.SetTable,
        Column.Builder.SetName,
        Column.Builder.SetAlias,
        Column.Builder.SetColumnType,
        Column.Builder.SetDataType,
        Column.Builder
{
    TableName table;
    String name;
    String alias;
    ColumnType columnType;
    SqlDataType dataType;

    @Override
    public SetName table( TableName table )
    {
        this.table = table;
        return this;
    }

    @Override
    public SetAlias name( String name )
    {
        this.name = name;
        return this;
    }

    @Override
    public SetColumnType alias( String alias )
    {
        this.alias = alias;
        return this;
    }

    @Override
    public SetDataType columnType( ColumnType columnType )
    {
        this.columnType = columnType;
        return this;
    }

    @Override
    public Column.Builder dataType( SqlDataType dataType )
    {
        this.dataType = dataType;
        return this;
    }

    @Override
    public Column build()
    {
        return new Column( this );
    }
}
