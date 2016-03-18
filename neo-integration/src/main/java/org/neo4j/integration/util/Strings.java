package org.neo4j.integration.util;

import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class Strings
{
    private static final String NEWLINE = System.lineSeparator();

    public static String lineSeparated( String... lines )
    {
        return asList( lines ).stream().collect( Collectors.joining( NEWLINE ) ).toString();
    }

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
