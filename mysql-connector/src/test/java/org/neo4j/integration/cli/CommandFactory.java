package org.neo4j.integration.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.neo4j.integration.util.LazyResource;
import org.neo4j.integration.util.OperatingSystem;
import org.neo4j.integration.util.Resource;
import org.neo4j.integration.util.TemporaryFile;

import static java.lang.String.format;

public class CommandFactory
{
    public static Resource<CommandFactory> newFactory()
    {
        LazyResource<Path> file =
                (LazyResource<Path>) TemporaryFile.temporaryFile( "cmd", OperatingSystem.isWindows() ? ".cmd" : ".sh" );

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

    private final Path file;
    private final boolean isWindows = OperatingSystem.isWindows();

    CommandFactory( Path file )
    {
        this.file = file;
    }

    public ProgramAndArguments echo( String value ) throws IOException
    {
        String script = isWindows ? format( "@echo off" + NEWLINE + "echo %s", value ) : format( "echo %s", value );
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
        String script = isWindows ? "@echo off" + NEWLINE + "echo %" + varName + "%" : format( "echo $%s", varName );
        String[] commands = toCommands( writeToFile( script ) );
        return new ProgramAndArguments( file, script, commands );
    }

    public ProgramAndArguments printWorkingDirectory() throws IOException
    {
        String script = isWindows ? "@echo off" + NEWLINE + "echo %cd%" : "pwd";
        String[] commands = toCommands( writeToFile( script ) );
        return new ProgramAndArguments( file, script, commands );
    }

    public ProgramAndArguments redirectStdInToStdOut() throws IOException
    {
        String script = "read a; echo $a;";
        String[] commands = toCommands( writeToFile( script ) );
        return new ProgramAndArguments( file, script, commands );
    }

    public ProgramAndArguments sleepSeconds( int seconds ) throws IOException
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
        private final Path file;
        private final String script;
        private final String[] commands;

        public ProgramAndArguments( Path file, String script, String[] commands )
        {
            this.file = file;
            this.script = script;
            this.commands = commands;
        }

        public Path file()
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
        Files.write( file, script.getBytes() );
        return file.toAbsolutePath().toString();
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
