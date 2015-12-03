package org.neo4j.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class StreamSinkTest
{
    @Test
    public void shouldCallOnLineForEachLineInStreamAndThenOnCompleted() throws IOException
    {
        // given
        String line1 = "line-1";
        String line2 = "line-2";
        String line3 = "line-3";

        StreamEventHandler eventHandler = mock( StreamEventHandler.class );

        PipedOutputStream output = new PipedOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter( output );

        InputStream input = new PipedInputStream( output );

        StreamSink streamSink = new StreamSink( input, eventHandler );

        writer.write( line1 );
        writer.write( System.lineSeparator() );
        writer.write( line2 );
        writer.write( System.lineSeparator() );
        writer.write( line3 );
        writer.write( System.lineSeparator() );

        writer.flush();
        writer.close();

        // when
        streamSink.run();

        // then
        verify( eventHandler ).onLine( line1 );
        verify( eventHandler ).onLine( line2 );
        verify( eventHandler ).onLine( line3 );
        verify( eventHandler ).onCompleted();
        verifyNoMoreInteractions( eventHandler );
    }

    @Test
    public void shouldCallOnExceptionAndThenOnCompletedOnEventHandlerWhenIOExceptionOccurs() throws IOException
    {
        // given
        IOException expectedException = new IOException( "Bad stream" );

        StreamEventHandler eventHandler = mock( StreamEventHandler.class );

        PipedOutputStream output = new PipedOutputStream();
        InputStream input = new ErrorThrowingInputStream( new PipedInputStream( output ), expectedException );

        StreamSink streamSink = new StreamSink( input, eventHandler );

        // when
        streamSink.run();

        // then
        verify( eventHandler ).onException( expectedException );
        verify( eventHandler ).onCompleted();
        verifyNoMoreInteractions( eventHandler );
    }

    @Test
    public void shouldOnlyCallOnCompletedOnEventHandlerWhenStreamClosedIOExceptionOccurs() throws IOException
    {
        // given
        IOException expectedException = new IOException( "Stream closed" );

        StreamEventHandler eventHandler = mock( StreamEventHandler.class );

        PipedOutputStream output = new PipedOutputStream();
        InputStream input = new ErrorThrowingInputStream( new PipedInputStream( output ), expectedException );

        StreamSink streamSink = new StreamSink( input, eventHandler );

        // when
        streamSink.run();

        // then
        verify( eventHandler ).onCompleted();
        verifyNoMoreInteractions( eventHandler );
    }
}
