package org.neo4j.integration.mysql.exportcsv.config;

import org.neo4j.integration.util.Preconditions;

import static java.lang.String.format;

public class TableName
{
    private final String name;

    public TableName( String name )
    {
        this.name = Preconditions.requireNonNullString( name, "Name" );
    }

    public String simpleName()
    {
        return name.substring( name.lastIndexOf( "." ) + 1 );
    }

    public String fullName()
    {
        return name;
    }

    public String formatColumn(String column)
    {
        return format("%s.%s", name, column);
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        TableName tableName = (TableName) o;

        return name.equals( tableName.name );

    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }
}
