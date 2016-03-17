package org.neo4j.integration.sql.metadata;

import org.neo4j.integration.util.Preconditions;

import static java.lang.String.format;

public class TableName
{
    private final String name;

    public TableName( String schema, String name )
    {
        this( format( "%s.%s", schema, name ) );
    }

    public TableName( String fullName )
    {
        this.name = Preconditions.requireNonNullString( fullName, "Name" );
    }

    public String schema()
    {
        int index = name.indexOf( "." );

        if ( index < 0 )
        {
            throw new IllegalArgumentException( format( "Table name does not include schema: %s", name ) );
        }

        return name.substring( 0, index );
    }

    public String simpleName()
    {
        return name.substring( name.lastIndexOf( "." ) + 1 );
    }

    public String fullName()
    {
        return name;
    }

    public String fullyQualifiedColumnName( String column )
    {
        return format( "%s.%s", name, column );
    }

    @Override
    public String toString()
    {
        return fullName();
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

        return name.equalsIgnoreCase( tableName.name );
    }

    @Override
    public int hashCode()
    {
        return name.toLowerCase().hashCode();
    }
}
