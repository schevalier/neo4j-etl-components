package org.neo4j.integration.mysql.metadata;

class ColumnBuilder implements Column.Builder.SetTable, Column.Builder.SetName, Column.Builder.SetType, Column.Builder
{
    TableName table;
    String name;
    ColumnType type;

    @Override
    public SetName table( TableName table )
    {
        this.table = table;
        return this;
    }

    @Override
    public SetType name( String name )
    {
        this.name = name;
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
