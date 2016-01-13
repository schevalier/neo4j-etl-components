package org.neo4j.integration.mysql.exportcsvnew.metadata;

import org.neo4j.integration.mysql.exportcsv.config.TableName;
import org.neo4j.integration.util.Preconditions;

public class Column
{
    public static Column.Builder.SetTable builder()
    {
        return new ColumnBuilder();
    }

    private final TableName table;
    private final String name;
    private final ColumnType type;

    Column( ColumnBuilder builder )
    {
        this.table = Preconditions.requireNonNull( builder.table, "Table" );
        this.name = Preconditions.requireNonNullString( builder.name, "Name" );
        this.type = Preconditions.requireNonNull( builder.type, "Type" );
    }

    public TableName table()
    {
        return table;
    }

    public String name()
    {
        return table.formatColumn( name );
    }

    public ColumnType type()
    {
        return type;
    }

    public interface Builder
    {
        interface SetTable
        {
            SetName table( TableName table );
        }

        interface SetName
        {
            SetType name( String name );
        }

        interface SetType
        {
            Builder type( ColumnType type );
        }

        Column build();
    }
}
