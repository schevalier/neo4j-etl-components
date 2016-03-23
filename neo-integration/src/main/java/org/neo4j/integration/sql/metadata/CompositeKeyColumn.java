package org.neo4j.integration.sql.metadata;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import org.neo4j.integration.sql.RowAccessor;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;

import static java.lang.String.format;

public class CompositeKeyColumn implements Column
{
    private static final String SEPARATOR = "\0";

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

    public Collection<Column> columns()
    {
        return columns;
    }

    @Override
    public TableName table()
    {
        return this.table;
    }

    @Override
    public String name()
    {
        return columns.stream().map( Column::name ).collect( Collectors.joining( SEPARATOR ) );
    }

    @Override
    public String alias()
    {
        return columns.stream().map( Column::alias ).collect( Collectors.joining( SEPARATOR ) );
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
        List<String> values = columns.stream()
                .map( c -> row.getString( c.alias() ) )
                .collect( Collectors.toList() );

        if ( values.stream().anyMatch( StringUtils::isEmpty ) )
        {
            return StringUtils.EMPTY;
        }
        else
        {
            return values.stream()
                    .filter( StringUtils::isNotEmpty )
                    .collect( Collectors.joining( SEPARATOR ) );
        }
    }

    @Override
    public String aliasedColumn()
    {
        return columns.stream().map( Column::aliasedColumn ).collect( Collectors.joining( ", " ) );
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

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this );
    }

    @Override
    public void addTo( ColumnToCsvFieldMappings.Builder builder )
    {
        columns.forEach( column -> column.addTo( builder ) );
    }
}
