package org.neo4j.command_line;

import org.neo4j.utils.OperatingSystem;

import static java.lang.String.format;

public class CommandFactory
{
    public static String echo( String value )
    {
        return OperatingSystem.isWindows() ? format( "@ECHO %s", value ) : format( "echo %s", value );
    }

    public static String echoToStdErr( String value )
    {
        return format( "echo %s>&2", value );
    }

    public static String exit( int exitValue )
    {
        return format( "exit %s", exitValue );
    }

    public static String echoEnvVar( String varName )
    {
        return OperatingSystem.isWindows() ? "@ECHO %" + varName + "%" : format( "echo $%s", varName );
    }

    public static String printWorkingDirectory()
    {
        return OperatingSystem.isWindows() ? "@ECHO %cd%" : "pwd";
    }

    public static String printNumbers( int maxValue )
    {
        if ( OperatingSystem.isWindows() )
        {
            return "echo off" + System.lineSeparator() +
                    "for /l %%x in (1, 1, " + maxValue + ") do (" + System.lineSeparator() +
                    "   echo %%x" + System.lineSeparator() +
                    "   ping -n 2 127.0.0.1 > nul" + System.lineSeparator() +
                    ")";
        }
        else
        {
            return "#!/bin/bash\n" +
                    "for i in `seq 1 " + maxValue + "`;\n" +
                    "do\n" +
                    "   echo $i\n" +
                    "   sleep 1s\n" +
                    "done";
        }
    }
}
