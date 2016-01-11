package org.neo4j.utils;

public class Strings
{
    public static String orNull( String value )
    {
        if ( value == null )
        {
            return null;
        }

        if ( value.trim().isEmpty() )
        {
            return null;
        }

        return value;
    }
}
