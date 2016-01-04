package org.neo4j.command_line;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.utils.OperatingSystem;
import org.neo4j.utils.ResourceRule;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import static org.neo4j.utils.TemporaryDirectory.temporaryDirectory;

public class CommandsTest
{
    @Rule
    public final ResourceRule<CommandFactory> commandFactory = new ResourceRule<>( CommandFactory.newFactory() );

    @Rule
    public final ResourceRule<File> tempDirectory = new ResourceRule<>( temporaryDirectory() );

    @Test
    public void shouldExecuteCommands() throws Exception
    {
        // given
        String expectedValue = "hello world";

        Commands commands = Commands.builder( commandFactory.get().echo( expectedValue ).commands() )
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

        Commands commands = Commands.builder( commandFactory.get().exit( expectedExitValue ).commands() )
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
        Commands commands = Commands.builder( commandFactory.get().exit( 1 ).commands() )
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
        Commands commands = Commands.builder( commandFactory.get().sleep( 1 ).commands() )
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
    public void shouldReturnNullIfCommandDurationExceedsAwaitTimeout() throws Exception
    {
        // given
        Commands commands = Commands.builder( commandFactory.get().sleep( 5 ).commands() )
                .inheritWorkingDirectory()
                .failOnNonZeroExitValue()
                .timeout( 10, TimeUnit.MILLISECONDS )
                .inheritEnvironment()
                .build();

        ResultHandle resultHandle = commands.execute();

        try
        {
            // when
            Result result = resultHandle.await( 2, TimeUnit.MILLISECONDS );

            // then
            assertNull( result );
        }
        finally
        {
            resultHandle.terminate();
        }
    }

    @Test
    public void shouldCaptureStdErrOutput() throws Exception
    {
        // given
        Commands commands = Commands.builder( commandFactory.get().echoToStdErr( "An error" ).commands() )
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

        Map<String, String> envVars = new HashMap<>();
        envVars.put( "MY_VAR", expectedValue );

        Commands commands = Commands.builder( commandFactory.get().echoEnvVar( "MY_VAR" ).commands() )
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
        Commands commands = Commands.builder( commandFactory.get().printWorkingDirectory().commands() )
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
            CommandFactory.ProgramAndArguments programAndArguments = commandFactory.get().redirectStdInToStdOut();

            Commands commands = Commands.builder( programAndArguments.commands() )
                    .inheritWorkingDirectory()
                    .failOnNonZeroExitValue()
                    .noTimeout()
                    .inheritEnvironment()
                    .redirectStdInFrom( ProcessBuilder.Redirect.from( programAndArguments.file() ) )
                    .build();

            // when
            Result result = commands.execute().await();

            // then
            assertEquals( 0, result.exitValue() );
            assertEquals( programAndArguments.script(), result.stdout() );
        }
    }

    @Test
    public void shouldThrowExceptionIfListOfCommandsIsEmpty()
    {
        try
        {
            // when
            Commands.commands();
            fail( "Expected IllegalArgumentException" );
        }
        catch ( IllegalArgumentException e )
        {
            // then
            assertEquals( "Commands cannot be empty", e.getMessage() );
        }
    }
}
