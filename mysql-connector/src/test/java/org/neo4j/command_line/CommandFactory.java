package org.neo4j.command_line;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import org.neo4j.utils.LazyResource;
import org.neo4j.utils.OperatingSystem;
import org.neo4j.utils.Resource;
import org.neo4j.utils.TemporaryFile;

import static java.lang.String.format;

public class CommandFactory
{
    public static Resource<CommandFactory> newFactory()
    {
        LazyResource<File> file =
                (LazyResource<File>) TemporaryFile.temporaryFile( "cmd", OperatingSystem.isWindows() ? ".cmd" : ".sh" );

        return new LazyResource<>( new LazyResource.Lifecycle<CommandFactory>()
        {
            @Override
            public CommandFactory create() throws Exception
            {
                return new CommandFactory( file.get() );
            }

            @Override
            public void destroy( CommandFactory commandFactory ) throws Exception
            {
                file.close();
            }
        } );
    }

    private static final String NEWLINE = System.lineSeparator();

    private final File file;
    private final boolean isWindows = OperatingSystem.isWindows();

    CommandFactory( File file )
    {
        this.file = file;
    }

    public ProgramAndArguments echo( String value ) throws IOException
    {
        String script = isWindows ? format( "@ECHO %s", value ) : format( "echo %s", value );
        String[] commands = toCommands( writeToFile( script ) );
        return new ProgramAndArguments( file, script, commands );
    }

    public ProgramAndArguments echoToStdErr( String value ) throws IOException
    {
        String script = format( "echo %s>&2", value );
        String[] commands = toCommands( writeToFile( script ) );
        return new ProgramAndArguments( file, script, commands );
    }

    public ProgramAndArguments exit( int exitValue ) throws IOException
    {
        String script = format( "exit %s", exitValue );
        String[] commands = toCommands( writeToFile( script ) );
        return new ProgramAndArguments( file, script, commands );
    }

    public ProgramAndArguments echoEnvVar( String varName ) throws IOException
    {
        String script = isWindows ? "@ECHO %" + varName + "%" : format( "echo $%s", varName );
        String[] commands = toCommands( writeToFile( script ) );
        return new ProgramAndArguments( file, script, commands );
    }

    public ProgramAndArguments printWorkingDirectory() throws IOException
    {
        String script = isWindows ? "@ECHO %cd%" : "pwd";
        String[] commands = toCommands( writeToFile( script ) );
        return new ProgramAndArguments( file, script, commands );
    }

    public ProgramAndArguments redirectStdInToStdOut() throws IOException
    {
        String script = "read a; echo $a;";
        String[] commands = toCommands( writeToFile( script ) );
        return new ProgramAndArguments( file, script, commands );
    }

    public ProgramAndArguments sleep( int seconds ) throws IOException
    {
        String script = isWindows ? format( "ping -n %s 127.0.0.1 > nul", seconds ) : format( "sleep %ss", seconds );
        String[] commands = toCommands( writeToFile( script ) );
        return new ProgramAndArguments( file, script, commands );
    }

    public ProgramAndArguments printNumbers( int maxValue ) throws IOException
    {
        String script = isWindows ?
                "echo off" + System.lineSeparator() +
                        "for /l %%x in (1, 1, " + maxValue + ") do (" + NEWLINE +
                        "   echo %%x" + System.lineSeparator() +
                        "   ping -n 2 127.0.0.1 > nul" + NEWLINE +
                        ")" :
                "#!/bin/bash\n" +
                        "for i in `seq 1 " + maxValue + "`;" + NEWLINE +
                        "do" + NEWLINE +
                        "   echo $i" + NEWLINE +
                        "   sleep 1s" + NEWLINE +
                        "done";

        String[] commands = toCommands( writeToFile( script ) );
        return new ProgramAndArguments( file, script, commands );
    }

    public static class ProgramAndArguments
    {
        private final File file;
        private final String script;
        private final String[] commands;

        public ProgramAndArguments( File file, String script, String[] commands )
        {
            this.file = file;
            this.script = script;
            this.commands = commands;
        }

        public File file()
        {
            return file;
        }

        public String script()
        {
            return script;
        }

        public String[] commands()
        {
            return commands;
        }
    }

    private String writeToFile( String script ) throws IOException
    {
        FileUtils.writeStringToFile( file, script );
        return file.getAbsolutePath();
    }

    private String[] toCommands( String script )
    {
        if ( OperatingSystem.isWindows() )
        {
            return new String[]{script};
        }
        else
        {
            return new String[]{"sh", script};
        }
    }
}
