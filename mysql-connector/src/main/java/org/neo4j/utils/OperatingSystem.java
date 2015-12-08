package org.neo4j.utils;

public class OperatingSystem
{
    public static boolean isWindows()
    {
        return System.getProperty( "os.name" ).startsWith( "Windows" );
    }
}
