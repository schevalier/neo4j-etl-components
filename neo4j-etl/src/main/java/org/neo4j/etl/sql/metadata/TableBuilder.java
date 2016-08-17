package org.neo4j.etl.sql.metadata;

import java.util.ArrayList;
import java.util.Collection;

class TableBuilder implements Table.Builder.SetName, Table.Builder
{
    TableName table;
    final Collection<Column> columns = new ArrayList<>();

    @Override
    public Table.Builder name( String name )
    {
        return name( new TableName( name ) );
    }

    @Override
    public Table.Builder name( TableName name )
    {
        this.table = name;
        return this;
    }

    @Override
    public Table.Builder addColumn( Column column )
    {
        columns.add( column );
        return this;
    }

    @Override
    public Table build()
    {
        return new Table( this );
    }
}
