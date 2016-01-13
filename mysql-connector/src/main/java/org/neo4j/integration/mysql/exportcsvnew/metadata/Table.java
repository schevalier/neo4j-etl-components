package org.neo4j.integration.mysql.exportcsvnew.metadata;

import java.util.Collection;
import java.util.Collections;

import org.neo4j.integration.mysql.exportcsv.config.TableName;
import org.neo4j.integration.util.Preconditions;

public class Table
{
    private final TableName name;
    private final Collection<Column> columns;

    Table( TableBuilder builder )
    {
        this.name = Preconditions.requireNonNull( builder.table, "Name" );
        this.columns = Collections.unmodifiableCollection(
                Preconditions.requireNonEmptyCollection( builder.columns, "Columns" ) );
    }

    public TableName name()
    {
        return name;
    }

    public Collection<Column> columns()
    {
        return columns;
    }

    public interface Builder
    {
        interface SetName
        {
            Builder name( String name );

            Builder name( TableName name );
        }

        Builder addColumn(String name, ColumnType type);

        Table build();
    }
}
