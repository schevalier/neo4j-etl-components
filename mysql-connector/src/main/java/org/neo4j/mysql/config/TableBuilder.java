package org.neo4j.mysql.config;

import java.util.ArrayList;
import java.util.Collection;

class TableBuilder implements Table.Builder.SetName, Table.Builder.SetFirstColumn, Table.Builder
{
    final Collection<Column> columns = new ArrayList<>();
    TableName name;

    @Override
    public Table.Builder.SetFirstColumn name( String name )
    {
        this.name = new TableName( name );
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
