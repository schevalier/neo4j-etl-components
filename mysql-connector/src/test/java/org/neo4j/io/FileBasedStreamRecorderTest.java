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

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.utils.ResourceRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import static org.neo4j.utils.TemporaryFile.temporaryFile;

public class FileBasedStreamRecorderTest
{
    /*
    A StreamSink's BufferedReader can throw an IOException from its readLine() method. In these tests we force the
    reader inside the StreamSink used by a FileBasedStreamReader to throw an exception while the
    FileBasedStreamRecorder is reading from a stream and writing to a file.
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

        StreamContentsHandle<FileDigest> contents = recorder.start( input );

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
            contents.await( 100, TimeUnit.MILLISECONDS ).file();
            fail( "Expected IOException" );
        }
        catch ( IOException e )
        {
            // then
            assertEquals( expectedException, e );
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
                (bufferSize * expectedNumberOfLines) + 1 );

        recorder.start( input );

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
    public void shouldHandleEmptyStream() throws IOException
    {
        // given
        FileBasedStreamRecorder recorder = new FileBasedStreamRecorder( tempFile.get() );

        PipedOutputStream output = new PipedOutputStream();
        InputStream input = new PipedInputStream( output );

        StreamContentsHandle<FileDigest> contents = recorder.start( input );

        Writer writer = new OutputStreamWriter( output );
        writer.close();

        // when
        String result = contents.await( 100, TimeUnit.MILLISECONDS ).toString();

        assertEquals( "", result );

    }

    private String createLine( int bufferSize, String a )
    {
        return new String( new char[bufferSize - 1] ).replace( "\0", a );
    }
}
