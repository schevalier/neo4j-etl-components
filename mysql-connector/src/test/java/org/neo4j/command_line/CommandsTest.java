package org.neo4j.command_line;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;

import org.neo4j.utils.OperatingSystem;
import org.neo4j.utils.ResourceRule;

import static java.lang.String.format;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import static org.neo4j.utils.TemporaryDirectory.temporaryDirectory;
import static org.neo4j.utils.TemporaryFile.temporaryFile;

public class CommandsTest
{
    @Rule
    public final ResourceRule<File> tempFile = new ResourceRule<>(
            temporaryFile( "tmp", OperatingSystem.isWindows() ? ".cmd" : ".sh" ) );

    @Rule
    public final ResourceRule<File> tempDirectory = new ResourceRule<>( temporaryDirectory() );

    private static final CommandFactory COMMAND_FACTORY = new CommandFactory( OperatingSystem.isWindows() );

    @Test
    public void shouldExecuteCommands() throws Exception
    {
        // given
        String expectedValue = "hello world";
        String script = createScript( COMMAND_FACTORY.echo( expectedValue ) );

        Commands commands = Commands.forCommands( toCommands( script ) )
                .inheritWorkingDirectory()
                .failOnNonZeroExitValue()
                .noTimeout()
                .inheritEnvironment()
                .build();

        // when
        Result result = commands.execute().await();

        // then
        assertEquals( 0, result.exitValue() );
        assertEquals( expectedValue, result.stdout() );
    }

    @Test
    public void shouldReturnExitCode() throws Exception
    {
        // given
        int expectedExitValue = 1;
        String script = createScript( COMMAND_FACTORY.exit( expectedExitValue ) );

        Commands commands = Commands.forCommands( toCommands( script ) )
                .inheritWorkingDirectory()
                .commandResultEvaluator( Result.Evaluator.IGNORE_FAILURES )
                .noTimeout()
                .inheritEnvironment()
                .build();

        // when
        Result result = commands.execute().await();

        // then
        assertEquals( expectedExitValue, result.exitValue() );
    }

    @Test
    public void shouldThrowExceptionIfCommandResultEvaluatorIndicatesFailure() throws Exception
    {
        // given
        String script = createScript( COMMAND_FACTORY.exit( 1 ) );

        Commands commands = Commands.forCommands( toCommands( script ) )
                .inheritWorkingDirectory()
                .failOnNonZeroExitValue()
                .noTimeout()
                .inheritEnvironment()
                .build();

        try
        {
            // when
            commands.execute().await();
            fail( "Expected Exception" );
        }
        catch ( Exception e )
        {
            // then
            assertThat( e.getMessage(), startsWith( "Command failed" ) );
        }
    }

    @Test
    public void shouldThrowExceptionIfCommandDurationExceedsTimeout() throws Exception
    {
        // given
        String script = createScript( "sleep 1s" );

        Commands commands = Commands.forCommands( toCommands( script ) )
                .inheritWorkingDirectory()
                .failOnNonZeroExitValue()
                .timeout( 5, TimeUnit.MILLISECONDS )
                .inheritEnvironment()
                .build();

        try
        {
            // when
            commands.execute().await();
            fail( "Expected Exception" );
        }
        catch ( Exception e )
        {
            // then
            assertThat( e.getCause(), instanceOf( TimeoutException.class ) );
            assertThat( e.getCause().getMessage(), startsWith( "Command failed to complete in a timely manner" ) );
        }
    }

    @Test
    public void shouldCaptureStdErrOutput() throws Exception
    {
        // given
        String script = createScript( COMMAND_FACTORY.echoToStdErr( "An error" ) );

        Commands commands = Commands.forCommands( toCommands( script ) )
                .inheritWorkingDirectory()
                .failOnNonZeroExitValue()
                .noTimeout()
                .inheritEnvironment()
                .build();

        // when
        Result result = commands.execute().await();

        // then
        assertEquals( "An error", result.stderr() );
    }

    @Test
    public void shouldAugmentEnvironment() throws Exception
    {
        // given
        String expectedValue = "env-var-value";
        String script = createScript( COMMAND_FACTORY.echoEnvVar( "MY_VAR" ) );

        Map<String, String> envVars = new HashMap<>();
        envVars.put( "MY_VAR", expectedValue );

        Commands commands = Commands.forCommands( toCommands( script ) )
                .inheritWorkingDirectory()
                .failOnNonZeroExitValue()
                .noTimeout()
                .augmentEnvironment( envVars )
                .build();

        // when
        Result result = commands.execute().await();

        // then
        assertEquals( expectedValue, result.stdout() );
    }

    @Test
    public void shouldChangeWorkingDirectory() throws Exception
    {
        // given
        String script = createScript( COMMAND_FACTORY.printWorkingDirectory() );

        Commands commands = Commands.forCommands( toCommands( script ) )
                .workingDirectory( tempDirectory.get() )
                .failOnNonZeroExitValue()
                .noTimeout()
                .inheritEnvironment()
                .build();

        // when
        Result result = commands.execute().await();

        // then
        assertEquals( tempDirectory.get().toPath().toRealPath(), new File( result.stdout() ).toPath().toRealPath() );
    }

    @Test
    public void shouldAllowRedirectingStdIn() throws Exception
    {
        if ( !OperatingSystem.isWindows() )
        {
            // given
            String script = createScript( "read a; echo $a;" );

            Commands commands = Commands.forCommands( toCommands( script ) )
                    .inheritWorkingDirectory()
                    .failOnNonZeroExitValue()
                    .noTimeout()
                    .inheritEnvironment()
                    .redirectStdInFrom( ProcessBuilder.Redirect.from( tempFile.get() ) )
                    .build();

            // when
            Result result = commands.execute().await();

            // then
            assertEquals( 0, result.exitValue() );
            assertEquals( "read a; echo $a;", result.stdout() );
        }
    }

    private String createScript( String script ) throws IOException
    {
        FileUtils.writeStringToFile( tempFile.get(), script );
        return tempFile.get().getAbsolutePath();
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

    private static class CommandFactory
    {
        private final boolean isWindows;

        private CommandFactory( boolean windows )
        {
            isWindows = windows;
        }

        public String echo( String value )
        {
            return isWindows ? format( "@ECHO %s", value ) : format( "echo %s", value );
        }

        public String echoToStdErr( String value )
        {
            return format( "echo %s>&2", value );
        }

        public String exit( int exitValue )
        {
            return format( "exit %s", exitValue );
        }

        public String echoEnvVar( String varName )
        {
            return isWindows ? "@ECHO %" + varName + "%" : format( "echo $%s", varName );
        }

        public String printWorkingDirectory()
        {
            return isWindows ? "@ECHO %cd%" : "pwd";
        }
    }
}
