package org.neo4j.mysql.config;

import org.neo4j.ingest.config.CsvField;
import org.neo4j.utils.Preconditions;

public class Column
{
    public static Builder.SetTable builder()
    {
        return new ColumnBuilder();
    }

    private final TableName table;
    private final String name;
    private final CsvField field;

    Column( ColumnBuilder builder )
    {
        this.table = Preconditions.requireNonNull( builder.table, "Table" );
        this.name = Preconditions.requireNonNullString( builder.name, "Name" );
        this.field = Preconditions.requireNonNull( builder.field, "Field" );
    }

    public TableName table()
    {
        return table;
    }

    public String name()
    {
        return table.formatColumn( name );
    }

    public CsvField field()
    {
        return field;
    }

    public interface Builder
    {
        interface SetTable
        {
            SetName table( TableName table );
        }

        interface SetName
        {
            SetField name( String name );
        }

        interface SetField
        {
            Builder mapsTo( CsvField field );
        }

        Column build();
    }
}
