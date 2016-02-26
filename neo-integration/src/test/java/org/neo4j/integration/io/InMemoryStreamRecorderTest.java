package org.neo4j.integration.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class InMemoryStreamRecorderTest
{
    private static final String NEWLINE = System.lineSeparator();

    @Test
    public void shouldThrowExceptionWhenAccessingValueIfUnderlyingStreamThrowsException() throws IOException
    {
        // given
        IOException expectedException = new IOException( "Bad stream" );

        InMemoryStreamRecorder recorder = new InMemoryStreamRecorder();

        PipedOutputStream output = new PipedOutputStream();
        InputStream input = new ErrorThrowingInputStream( new PipedInputStream( output ), expectedException );

        new StreamSink( input, recorder ).start();

        Writer writer = new OutputStreamWriter( output );

        writer.write( "A" );
        writer.write( NEWLINE );
        writer.write( "B" );
        writer.write( NEWLINE );
        writer.write( "C" );
        writer.write( NEWLINE );

        writer.flush();
        writer.close();

        try
        {
            // when
            //noinspection ResultOfMethodCallIgnored
            recorder.awaitContents( 1, TimeUnit.SECONDS );
            fail( "Expected IOException" );
        }
        catch ( IOException e )
        {
            // then
            assertEquals( expectedException, e );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldStoreStreamContentsInMemory() throws Exception
    {
        // given
        InMemoryStreamRecorder recorder = new InMemoryStreamRecorder();

        PipedOutputStream output = new PipedOutputStream();
        InputStream input = new PipedInputStream( output );

        new StreamSink( input, recorder ).start();

        Writer writer = new OutputStreamWriter( output );

        writer.write( "A" );
        writer.write( NEWLINE );
        writer.write( "B" );
        writer.write( NEWLINE );
        writer.write( "C" );
        writer.write( NEWLINE );

        writer.flush();
        writer.close();

        // when
        String contents = recorder.awaitContents( 1, TimeUnit.SECONDS );

        // then
        String expectedContents = "A" + NEWLINE + "B" + NEWLINE + "C";

        assertEquals( expectedContents, contents );
    }

    @Test
    public void shouldHandleEmptyStream() throws Exception
    {
        // given
        InMemoryStreamRecorder recorder = new InMemoryStreamRecorder();

        PipedOutputStream output = new PipedOutputStream();
        InputStream input = new PipedInputStream( output );

        new StreamSink( input, recorder ).start();

        Writer writer = new OutputStreamWriter( output );
        writer.close();

        // when
        String result = recorder.awaitContents( 1, TimeUnit.SECONDS );

        // then
        assertEquals( "", result );
    }

    @Test
    public void shouldAbbreviateContentsIfNumberOfLinesExceedsConfiguredMaximum() throws Exception
    {
        // given
        InMemoryStreamRecorder recorder = new InMemoryStreamRecorder( 5 );

        PipedOutputStream output = new PipedOutputStream();
        InputStream input = new PipedInputStream( output );

        new StreamSink( input, recorder ).start();

        Writer writer = new OutputStreamWriter( output );

        for ( int i = 0; i < 10; i++ )
        {
            writer.write( String.valueOf( i ) );
            writer.write( NEWLINE );
        }

        writer.flush();
        writer.close();

        // when
        String contents = recorder.awaitContents( 1, TimeUnit.SECONDS );

        // then
        String expectedContents = "0" + NEWLINE
                + "1" + NEWLINE
                + "2" + NEWLINE
                + "3" + NEWLINE
                + "4" + NEWLINE
                + "[...]" + NEWLINE
                + "9";

        assertEquals( expectedContents, contents );
    }
}
