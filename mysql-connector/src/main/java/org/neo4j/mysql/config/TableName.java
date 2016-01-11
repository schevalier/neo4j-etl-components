package org.neo4j.mysql.config;

import org.neo4j.utils.Preconditions;

public class TableName
{
    private final String value;

    TableName( String value )
    {
        this.value = Preconditions.requireNonNullString( value, "Value" );
    }

    public String simpleValue()
    {
        return value.substring( value.lastIndexOf( "." ) + 1 );
    }

    public String value()
    {
        return value;
    }
}
