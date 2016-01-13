package org.neo4j.integration.mysql.exportcsvnew.metadata;

import org.neo4j.integration.mysql.exportcsv.config.TableName;

class ColumnBuilder implements Column.Builder.SetTable, Column.Builder.SetName, Column.Builder.SetType, Column.Builder
{
    TableName table;
    String name;
    ColumnType type;

    @Override
    public SetName table( TableName table )
    {
        this.table = table;
        return null;
    }

    @Override
    public SetType name( String name )
    {
        this.name = name;
        return null;
    }

    @Override
    public Column.Builder type( ColumnType type )
    {
        this.type = type;
        return null;
    }

    @Override
    public Column build()
    {
        return new Column( this );
    }
}
