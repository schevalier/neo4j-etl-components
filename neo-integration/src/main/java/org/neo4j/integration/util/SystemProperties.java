package org.neo4j.integration.util;

import java.util.Optional;

import static java.lang.System.getProperty;

public class SystemProperties
{
    public static String asString( String key )
    {
        return getProperty( key );
    }

    public static Optional<String> asOptionalString( String key )
    {
        return Optional.ofNullable( asString( key ) );
    }

    public static boolean asBoolean( String key )
    {
        return Boolean.parseBoolean( asOptionalString( key ).orElse( "false" ) );
    }
}
