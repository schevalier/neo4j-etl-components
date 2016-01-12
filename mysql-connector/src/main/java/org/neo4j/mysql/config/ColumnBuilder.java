package org.neo4j.mysql.config;

import org.neo4j.ingest.config.CsvField;

class ColumnBuilder implements Column.Builder, Column.Builder.SetTable, Column.Builder.SetName, Column.Builder.SetField
{
    TableName table;
    String name;
    CsvField field;

    @Override
    public SetName table( TableName table )
    {
        this.table = table;
        return this;
    }

    @Override
    public SetField name( String name )
    {
        this.name = name;
        return this;
    }

    @Override
    public Column.Builder mapsTo( CsvField field )
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
