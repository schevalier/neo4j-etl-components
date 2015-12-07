package org.neo4j.io;

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
        writer.write( System.lineSeparator() );
        writer.write( "B" );
        writer.write( System.lineSeparator() );
        writer.write( "C" );
        writer.write( System.lineSeparator() );

        writer.flush();
        writer.close();

        try
        {
            // when
            //noinspection ResultOfMethodCallIgnored
            recorder.awaitContents( 100, TimeUnit.MILLISECONDS );
            fail( "Expected IOException" );
        }
        catch ( IOException e )
        {
            // then
            assertEquals( expectedException, e );
        }
    }

    @Test
    public void shouldStoreStreamContentsInMemory() throws IOException
    {
        // given
        InMemoryStreamRecorder recorder = new InMemoryStreamRecorder();

        PipedOutputStream output = new PipedOutputStream();
        InputStream input = new PipedInputStream( output );

        new StreamSink( input, recorder ).start();

        Writer writer = new OutputStreamWriter( output );

        writer.write( "A" );
        writer.write( System.lineSeparator() );
        writer.write( "B" );
        writer.write( System.lineSeparator() );
        writer.write( "C" );
        writer.write( System.lineSeparator() );

        writer.flush();
        writer.close();

        // when
        String contents = recorder.awaitContents( 10, TimeUnit.MILLISECONDS );

        // then
        String expectedContents = "A" + NEWLINE + "B" + NEWLINE + "C" + NEWLINE;

        assertEquals( expectedContents, contents );
    }

    @Test
    public void shouldHandleEmptyStream() throws IOException
    {
        // given
        InMemoryStreamRecorder recorder = new InMemoryStreamRecorder();

        PipedOutputStream output = new PipedOutputStream();
        InputStream input = new PipedInputStream( output );

        new StreamSink( input, recorder ).start();

        Writer writer = new OutputStreamWriter( output );
        writer.close();

        // when
        String result = recorder.awaitContents( 100, TimeUnit.MILLISECONDS );

        // then
        assertEquals( "", result );
    }
}
