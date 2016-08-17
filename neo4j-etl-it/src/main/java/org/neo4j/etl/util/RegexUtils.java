package org.neo4j.etl.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class RegexUtils
{
    public static Matcher match( String value, String regex, String name )
    {
        return match( value, Pattern.compile( regex ), name );
    }

    public static Matcher match( String value, Pattern pattern, String name )
    {
        Matcher matcher = pattern.matcher( value );
        if ( !matcher.matches() )
        {
            throw new IllegalArgumentException(
                    format( "Cannot recognize '%s' as a %s using regex '%s'.", value, name, pattern.pattern() ) );
        }
        return matcher;
    }
}
