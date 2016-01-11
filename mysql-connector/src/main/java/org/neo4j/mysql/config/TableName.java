package org.neo4j.mysql.config;

import org.neo4j.utils.Preconditions;

import static java.lang.String.format;

public class TableName
{
    private final String name;

    TableName( String name )
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
}
