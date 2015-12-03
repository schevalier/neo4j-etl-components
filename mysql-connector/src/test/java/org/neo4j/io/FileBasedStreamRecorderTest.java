package org.neo4j.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.utils.ResourceRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import static org.neo4j.utils.TemporaryFile.temporaryFile;

public class FileBasedStreamRecorderTest
{
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

        OutputStreamWriter writer = new OutputStreamWriter( output );
        writer.write( "A\n" );
        writer.write( "B\n" );
        writer.write( "C\n" );

        writer.flush();
        writer.close();

        try
        {
            // when
            //noinspection ResultOfMethodCallIgnored
            contents.await( 5, TimeUnit.SECONDS ).file();
            fail( "Expected IOException" );
        }
        catch ( IOException e )
        {
            // then
            assertEquals( expectedException, e );
        }
    }
}
