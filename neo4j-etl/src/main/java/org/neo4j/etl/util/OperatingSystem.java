package org.neo4j.etl.util;

public class OperatingSystem
{
    public static boolean isWindows()
    {
        return System.getProperty( "os.name" ).startsWith( "Windows" );
    }
}
