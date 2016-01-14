package org.neo4j.integration.sql.metadata;

import java.util.Collection;
import java.util.Collections;

import org.neo4j.integration.util.Preconditions;

public class Table implements DatabaseObject
{
    public static Builder.SetName builder()
    {
        return new TableBuilder();
    }

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

    @Override
    public String descriptor()
    {
        return name.fullName();
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
