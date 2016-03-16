package org.neo4j.integration.sql.metadata;

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.neo4j.integration.sql.RowAccessor;
import org.neo4j.integration.util.StringListBuilder;

import static java.lang.String.format;

public class CompositeKeyColumn implements Column
{
    private final TableName table;
    private final Collection<Column> columns;

    public CompositeKeyColumn( TableName table, Collection<Column> columns )
    {
        this.table = table;
        this.columns = columns;

        for ( Column column : columns )
        {
            if ( !column.table().equals( table ) )
            {
                throw new IllegalArgumentException(
                        format( "Column '%s' is not associated with table '%s", column.name(), table.fullName() ) );
            }
        }
    }

    @Override
    public TableName table()
    {
        return this.table;
    }

    @Override
    public String name()
    {
        return StringListBuilder.stringList( columns, "_", Column::name ).toString();
    }

    @Override
    public String alias()
    {
        return StringListBuilder.stringList( columns, "_", Column::alias ).toString();
    }

    @Override
    public ColumnType type()
    {
        return ColumnType.CompositeKey;
    }

    @Override
    public SqlDataType dataType()
    {
        return SqlDataType.COMPOSITE_KEY_TYPE;
    }

    @Override
    public String selectFrom( RowAccessor row )
    {
        return columns.stream()
                .map( c -> row.getString( c.alias() ) )
                .collect( Collectors.joining( "_" ) );
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals( Object o )
    {
        return EqualsBuilder.reflectionEquals( this, o );
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode( this );
    }
}
