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

    private final String name;
    private final Collection<Column> columns;

    Table( TableBuilder builder )
    {
        this.name = Preconditions.requireNonNullString( builder.name, "Table name cannot be null or empty string" );
        this.columns = Collections.unmodifiableCollection(
                Preconditions.requireNonEmptyCollection( builder.columns, "Columns cannot be empty" ) );
    }

    @Override
    public Collection<Field> fieldMappings()
    {
        return columns.stream().map( Column::field ).collect( Collectors.toList() );
    }

    public String simpleName()
    {
        return name.substring( name.lastIndexOf( "." ) + 1 );
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
