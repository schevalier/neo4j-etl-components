package org.neo4j.mysql.config;

import org.neo4j.ingest.config.Field;

class ColumnBuilder implements Column.Builder, Column.Builder.SetName, Column.Builder.SetField
{
    String name;
    Field field;

    @Override
    public SetField name( String name )
    {
        this.name = name;
        return this;
    }

    @Override
    public Column.Builder mapsTo( Field field )
    {
        this.field = field;
        return this;
    }

    @Override
    public Column build()
    {
        return new Column( this );
    }
}
