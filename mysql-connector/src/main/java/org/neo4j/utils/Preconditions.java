package org.neo4j.utils;

import java.util.Collection;
import java.util.List;

public class Preconditions
{
    public static String requireNonNullString( String value, String message )
    {
        if ( value == null )
        {
            throw new NullPointerException( message );
        }

        if ( value.trim().isEmpty() )
        {
            throw new IllegalArgumentException( message );
        }

        return value;
    }

    public static <T> Collection<T> requireNonEmptyCollection( Collection<T> value, String message )
    {
        if ( value.isEmpty() )
        {
            throw new IllegalArgumentException( message );
        }

        return value;
    }

    public static <T> List<T> requireNonEmptyList( List<T> value, String message )
    {
        if ( value.isEmpty() )
        {
            throw new IllegalArgumentException( message );
        }

        return value;
    }
}
