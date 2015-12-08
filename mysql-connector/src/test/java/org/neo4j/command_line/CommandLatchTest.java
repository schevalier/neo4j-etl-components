package org.neo4j.command_line;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;

import org.neo4j.utils.ResourceRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static org.neo4j.utils.TemporaryFile.temporaryFile;

public class CommandLatchTest
{
    @Rule
    public final ResourceRule<File> tempFile = new ResourceRule<>( temporaryFile( "tmp", ".sh" ) );

    @Test
    public void shouldReturnOkWhenPredicateSatisfied() throws Exception
    {
        // given
        String script = createScript( "#!/bin/bash\n" +
                "        for i in `seq 1 10`;\n" +
                "        do\n" +
                "                echo $i\n" +
                "                sleep 0.1s\n" +
                "        done" );

        CommandLatch latch = new CommandLatch( l -> Integer.valueOf( l ) == 5 );

        Commands commands = Commands.forCommands( "sh", script )
                .inheritWorkingDirectory()
                .failOnNonZeroExitValue()
                .noTimeout()
                .inheritEnvironment()
                .redirectStdOutTo( latch )
                .build();

        ResultHandle resultHandle = commands.execute();

        // when
        CommandLatch.CommandLatchResult result = latch.awaitContents( 1, TimeUnit.SECONDS );

        // then
        assertTrue( result.ok() );
        resultHandle.terminate();
    }

    @Test
    public void shouldReturnNotOkWhenPredicateNotSatisfied() throws Exception
    {
        // given
        String script = createScript( "#!/bin/bash\n" +
                "        for i in `seq 1 10`;\n" +
                "        do\n" +
                "                echo $i\n" +
                "                sleep 0.1s\n" +
                "        done" );

        CommandLatch latch = new CommandLatch( l -> Integer.valueOf( l ) == 11 );

        Commands commands = Commands.forCommands( "sh", script )
                .inheritWorkingDirectory()
                .failOnNonZeroExitValue()
                .noTimeout()
                .inheritEnvironment()
                .redirectStdOutTo( latch )
                .build();

        ResultHandle resultHandle = commands.execute();

        // when
        CommandLatch.CommandLatchResult result = latch.awaitContents( 2, TimeUnit.SECONDS );

        // then
        assertFalse( result.ok() );
        resultHandle.terminate();
    }

    @Test
    public void shouldThrowExceptionWhenPredicateThrowsException() throws Exception
    {
        // given
        String script = createScript( "#!/bin/bash\n" +
                "        for i in `seq 1 10`;\n" +
                "        do\n" +
                "                echo $i\n" +
                "                sleep 0.1s\n" +
                "        done" );

        CommandLatch latch = new CommandLatch( l -> {
            if ( Integer.valueOf( l ) == 5 )
            {
                throw new IOException( "Illegal value: 5" );
            }
            return Integer.valueOf( l ) == 6;
        } );

        Commands commands = Commands.forCommands( "sh", script )
                .inheritWorkingDirectory()
                .failOnNonZeroExitValue()
                .noTimeout()
                .inheritEnvironment()
                .redirectStdOutTo( latch )
                .build();

        ResultHandle resultHandle = commands.execute();

        try
        {
            // when
            latch.awaitContents( 2, TimeUnit.SECONDS );
            fail( "Expected IOException" );
        }
        catch ( IOException e )
        {
            // then
            assertEquals( "Illegal value: 5", e.getMessage() );
        }
        finally
        {
            resultHandle.terminate();
        }
    }

    private String createScript( String script ) throws IOException
    {
        FileUtils.writeStringToFile( tempFile.get(), script );
        return tempFile.get().getAbsolutePath();
    }
}
