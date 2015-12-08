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
                "        for i in `seq 1 20`;\n" +
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

        long startTime = System.currentTimeMillis();

        try ( ResultHandle ignored = commands.execute() )
        {
            // when
            CommandLatch.CommandLatchResult result = latch.awaitContents( 1, TimeUnit.SECONDS );
            long endTime = System.currentTimeMillis();

            // then
            assertTrue( result.ok() );
            assertTrue( endTime - startTime < TimeUnit.SECONDS.toMillis( 1 ) );
        }
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

        long startTime = System.currentTimeMillis();

        try ( ResultHandle ignored = commands.execute() )
        {
            // when
            CommandLatch.CommandLatchResult result = latch.awaitContents( 2, TimeUnit.SECONDS );
            long endTime = System.currentTimeMillis();

            // then
            assertFalse( result.ok() );
            assertTrue( endTime - startTime >= TimeUnit.SECONDS.toMillis( 1 ) );
        }
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

        IOException expectedException = new IOException( "Illegal value: 5" );

        CommandLatch latch = new CommandLatch( l -> {
            if ( Integer.valueOf( l ) == 5 )
            {
                throw expectedException;
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

        long startTime = System.currentTimeMillis();

        try ( ResultHandle ignored = commands.execute() )
        {
            // when
            latch.awaitContents( 2, TimeUnit.SECONDS );
            fail( "Expected IOException" );
        }
        catch ( IOException e )
        {
            // then
            assertEquals( expectedException, e );

            long endTime = System.currentTimeMillis();
            assertTrue( endTime - startTime < TimeUnit.SECONDS.toMillis( 1 ) );
        }
    }

    private String createScript( String script ) throws IOException
    {
        FileUtils.writeStringToFile( tempFile.get(), script );
        return tempFile.get().getAbsolutePath();
    }
}
