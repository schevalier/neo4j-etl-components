package org.neo4j.integration.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Preconditions
{
    public static <T> T requireNonNull( T value, String message )
    {
        if ( value == null )
        {
            throw new NullPointerException( message + " cannot be null" );
        }

        return value;
    }

    public static String requireNonNullString( String value, String message )
    {
        if ( value == null )
        {
            throw new NullPointerException( message + " cannot be null" );
        }

        if ( value.trim().isEmpty() )
        {
            throw new IllegalArgumentException( message + " cannot be empty" );
        }

        return value;
    }

    public static <T> Collection<T> requireNonEmptyCollection( Collection<T> value, String message )
    {
        if ( value.isEmpty() )
        {
            throw new IllegalArgumentException( message + " cannot be empty" );
        }

        return value;
    }

    public static <T> List<T> requireNonEmptyList( List<T> value, String message )
    {
        if ( value.isEmpty() )
        {
            throw new IllegalArgumentException( message + " cannot be empty" );
        }

        return value;
    }

    public static <K, V> Map<K, V> requireNonEmptyMap( Map<K, V> value, String message )
    {
        if ( value.isEmpty() )
        {
            throw new IllegalArgumentException( message + " cannot be empty" );
        }

        return value;
    }
}
