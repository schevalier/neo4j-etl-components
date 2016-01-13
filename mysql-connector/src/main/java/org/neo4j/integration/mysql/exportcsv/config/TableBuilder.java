package org.neo4j.integration.mysql.exportcsv.config;

import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.config.CsvField;
import org.neo4j.integration.neo4j.importcsv.config.IdSpace;

class TableBuilder implements Table.Builder.SetName, Table.Builder.SetId, Table.Builder.SetFirstColumn, Table.Builder
{
    final Collection<Column> columns = new ArrayList<>();
    TableName table;
    Column id;

    @Override
    public Table.Builder.SetId name( String name )
    {
        this.table = new TableName( name );
        return this;
    }

    @Override
    public Table.Builder.SetId name( TableName name )
    {
        this.table = name;
        return this;
    }

    @Override
    public Table.Builder addColumn( String column, CsvField field )
    {
        columns.add( Column.builder().table( table ).name( column ).mapsTo( field ).build() );
        return this;
    }

    @Override
    public Table build()
    {
        return new Table( this );
    }

    @Override
    public SetFirstColumn id( String column )
    {
        id = Column.builder()
                .table( table )
                .name( column )
                .mapsTo( CsvField.id( new IdSpace( table.simpleName() ) ) )
                .build();
        return this;
    }

    @Override
    public SetFirstColumn id( String column, String fieldName )
    {
        id = Column.builder()
                .table( table )
                .name( column )
                .mapsTo( CsvField.id( fieldName, new IdSpace( table.simpleName() ) ) )
                .build();
        return this;
    }
}