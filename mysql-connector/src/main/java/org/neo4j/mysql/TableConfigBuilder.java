package org.neo4j.mysql;

import java.util.ArrayList;
import java.util.Collection;

class TableConfigBuilder implements TableConfig.Builder.SetName, TableConfig.Builder
{
    final Collection<Column> columns = new ArrayList<>(  );
    String name;

    @Override
    public TableConfig.Builder name( String name )
    {
        this.name = name;
        return this;
    }

    @Override
    public TableConfig.Builder addColumn( Column column )
    {
        columns.add( column );
        return this;
    }

    @Override
    public TableConfig build()
    {
        return new TableConfig( this );
    }
}
