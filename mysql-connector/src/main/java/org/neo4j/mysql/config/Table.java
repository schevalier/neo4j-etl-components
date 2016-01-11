package org.neo4j.mysql.config;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.neo4j.ingest.config.Field;
import org.neo4j.ingest.config.FieldMappings;
import org.neo4j.utils.Preconditions;


public class Table implements FieldMappings
{
    public static Builder.SetName builder()
    {
        return new TableBuilder();
    }

    private final TableName name;
    private final Collection<Column> columns;

    Table( TableBuilder builder )
    {
        this.name = Preconditions.requireNonNull( builder.name, "Table name" );
        this.columns = Collections.unmodifiableCollection(
                Preconditions.requireNonEmptyCollection( builder.columns, "Columns" ) );
    }

    @Override
    public Collection<Field> fieldMappings()
    {
        return columns.stream().map( Column::field ).collect( Collectors.toList() );
    }

    public Collection<String> columns()
    {
        return columns.stream().map( Column::name ).collect( Collectors.toList() );
    }

    public TableName name()
    {
        return name;
    }

    public interface Builder
    {
        interface SetName
        {
            SetFirstColumn name( String name );
        }

        interface SetFirstColumn
        {
            Builder addColumn( Column column );
        }

        Builder addColumn( Column column );

        Table build();
    }
}
