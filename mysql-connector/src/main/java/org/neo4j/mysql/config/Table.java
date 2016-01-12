package org.neo4j.mysql.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.neo4j.ingest.config.CsvField;
import org.neo4j.ingest.config.FieldMappings;
import org.neo4j.utils.Preconditions;


public class Table implements FieldMappings
{
    public static Builder.SetName builder()
    {
        return new TableBuilder();
    }

    private final TableName name;
    private final Column id;
    private final Collection<Column> columns;

    Table( TableBuilder builder )
    {
        this.name = Preconditions.requireNonNull( builder.table, "Table name" );
        this.id = Preconditions.requireNonNull( builder.id, "Ide" );
        this.columns = Collections.unmodifiableCollection(
                Preconditions.requireNonEmptyCollection( builder.columns, "Columns" ) );
    }

    @Override
    public Collection<CsvField> fieldMappings()
    {
        return all( id.field(), columns.stream().map( Column::field ).collect( Collectors.toList() ) );
    }

    public Collection<String> columns()
    {
        return all( id.name(), columns.stream().map( Column::name ).collect( Collectors.toList() ) );
    }

    public TableName name()
    {
        return name;
    }

    private <T> Collection<T> all( T first, List<T> remaining )
    {
        remaining.add( 0, first );
        return remaining;
    }

    public interface Builder
    {
        interface SetName
        {
            SetId name( String name );

            SetId name( TableName name );
        }

        interface SetId
        {
            SetFirstColumn id( String column );

            SetFirstColumn id( String column, String fieldName );
        }

        interface SetFirstColumn
        {
            Builder addColumn( String column, CsvField field );
        }

        Builder addColumn( String column, CsvField field );

        Table build();
    }
}
