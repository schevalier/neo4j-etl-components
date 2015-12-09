package org.neo4j.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;

import org.neo4j.utils.ResourceRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import static org.neo4j.utils.TemporaryFile.temporaryFile;

public class FileBasedStreamRecorderTest
{
    private static final String NEWLINE = System.lineSeparator();

    /*
    A StreamSink's BufferedReader can throw an IOException from its readLine() method. In these tests we force the
    reader inside a StreamSink to throw an exception while its FileBasedStreamRecorder is reading from the stream
    and writing to a file.
     */

    @Rule
    public final ResourceRule<File> tempFile = new ResourceRule<>( temporaryFile() );

    @Test
    public void shouldThrowExceptionWhenAccessingValueIfUnderlyingStreamThrowsException() throws IOException
    {
        // given
        IOException expectedException = new IOException( "Bad stream" );

        FileBasedStreamRecorder recorder = new FileBasedStreamRecorder( tempFile.get() );

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
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldBeAbleToReadFileUpToPointExceptionOccurred() throws IOException
    {
        // given
        int bufferSize = 8192; // This value fixed by StreamDecoder used by the StreamSink's InputStreamReader
        int expectedNumberOfLines = 2;

        String line1 = createLine( bufferSize, "A" );
        String line2 = createLine( bufferSize, "B" );
        String line3 = createLine( bufferSize, "C" );

        FileBasedStreamRecorder recorder = new FileBasedStreamRecorder( tempFile.get() );

        PipedOutputStream output = new PipedOutputStream();
        InputStream input = new ErrorThrowingInputStream(
                new PipedInputStream( output ),
                new IOException( "Bad stream" ),
                (bufferSize * expectedNumberOfLines) + (bufferSize / 8) );

        new StreamSink( input, recorder ).start();

        try
        {
            Writer writer = new OutputStreamWriter( output );
            writer.write( line1 );
            writer.write( System.lineSeparator() );
            writer.write( line2 );
            writer.write( System.lineSeparator() );
            writer.write( line3 );
            writer.write( System.lineSeparator() );

            writer.flush();
            writer.close();
        }
        catch ( IOException e )
        {
            // Do nothing
        }

        // when
        // then
        List<String> lines = Files.readAllLines( tempFile.get().toPath() );

        assertEquals( expectedNumberOfLines, lines.size() );
        assertEquals( line1, lines.get( 0 ) );
        assertEquals( line2, lines.get( 1 ) );
    }

    @Test
    public void shouldHandleEmptyStream() throws Exception
    {
        // given
        FileBasedStreamRecorder recorder = new FileBasedStreamRecorder( tempFile.get() );

        PipedOutputStream output = new PipedOutputStream();
        InputStream input = new PipedInputStream( output );

        new StreamSink( input, recorder ).start();

        Writer writer = new OutputStreamWriter( output );
        writer.close();

        // when
        String result = recorder.awaitContents( 100, TimeUnit.MILLISECONDS ).toString();

        // then
        assertEquals( "", result );
    }

    @Test
    public void shouldStoreStreamContentsInFile() throws Exception
    {
        // given
        FileBasedStreamRecorder recorder = new FileBasedStreamRecorder( tempFile.get() );

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
        File file = recorder.awaitContents( 100, TimeUnit.MILLISECONDS ).file();

        // then
        String expectedContents = "A" + NEWLINE + "B" + NEWLINE + "C" + NEWLINE;

        assertEquals( expectedContents, FileUtils.readFileToString( file ) );
    }

    private String createLine( int bufferSize, String a )
    {
        return new String( new char[bufferSize - 1] ).replace( "\0", a );
    }
}
