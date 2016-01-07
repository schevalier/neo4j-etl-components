package org.neo4j.mysql.config;

import org.neo4j.ingest.config.Field;

class ColumnBuilder implements Column.Builder
{
    final String name;
    Field field;

    public ColumnBuilder( String name )
    {
        this.name = name;
    }

    @Override
    public Column mapsTo( Field field )
    {
        this.field = field;
        return new Column( this );
    }
}
