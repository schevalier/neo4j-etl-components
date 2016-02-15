package org.neo4j.integration.sql.metadata;

class ColumnBuilder implements Column.Builder.SetTable,
        Column.Builder.SetName,
        Column.Builder.SetAlias,
        Column.Builder.SetType,
        Column.Builder
{
    TableName table;
    String name;
    String alias;
    ColumnType type;

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
    public SetType alias( String alias )
    {
        this.alias = alias;
        return this;
    }

    @Override
    public Column.Builder type( ColumnType type )
    {
        this.type = type;
        return this;
    }

    @Override
    public Column build()
    {
        return new Column( this );
    }
}
