package org.neo4j.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import org.neo4j.utils.FutureUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PipeTest
{
    private static final String NEWLINE = System.lineSeparator();

    @Test
    public void shouldCreateNamedPipeForWriting() throws Exception
    {
        // given
        String expectedResults = "line 1" + NEWLINE + "line 2" + NEWLINE;
        StringBuilder results = new StringBuilder();

        String name = UUID.randomUUID().toString();

        // when
        try ( Pipe pipe = new Pipe( name ) )
        {
            CompletableFuture<OutputStream> out = pipe.out();
            CompletableFuture<InputStream> in = pipe.in();

            try ( Writer writer = new OutputStreamWriter( out.get() );
                  BufferedReader reader = new BufferedReader( new InputStreamReader( in.get() ) ) )
            {
                writer.write( "line 1" );
                writer.write( NEWLINE );
                writer.write( "line 2" );
                writer.write( NEWLINE );
                writer.write( NEWLINE );
                writer.flush();

                String line;

                while ( (line = reader.readLine()) != null && !line.equals( "" ) )
                {
                    results.append( line ).append( NEWLINE );
                }
            }
        }

        // then
        assertEquals( expectedResults, results.toString() );
    }

    @Test
    public void shouldFailOutStreamFastWhenExceptionableThrowsAnException() throws Exception
    {
        // given
        try ( Pipe pipe = new Pipe( UUID.randomUUID().toString() ) )
        {
            IOException expectedException = new IOException( "IO error" );

            CompletableFuture<Object> exceptionable = FutureUtils.exceptionableFuture( () -> {
                throw expectedException;
            } );

            CompletableFuture<OutputStream> out = pipe.out( exceptionable );

            try
            {
                // when
                out.get();
                fail( "Expected ExecutionException" );
            }
            catch ( ExecutionException e )
            {
                // then
                assertEquals( expectedException, e.getCause().getCause() );
            }
        }
    }

    @Test
    public void shouldFailInStreamFastWhenExceptionableThrowsAnException() throws Exception
    {
        // given
        try ( Pipe pipe = new Pipe( UUID.randomUUID().toString() ) )
        {
            IOException expectedException = new IOException( "IO error" );

            CompletableFuture<Object> exceptionable = FutureUtils.exceptionableFuture( () -> {
                throw expectedException;
            } );

            CompletableFuture<InputStream> out = pipe.in( exceptionable );

            try
            {
                // when
                out.get();
                fail( "Expected ExecutionException" );
            }
            catch ( ExecutionException e )
            {
                // then
                assertEquals( expectedException, e.getCause().getCause() );
            }
        }
    }

    @Test
    public void shouldDeleteFileOnClose() throws Exception
    {
        // given
        String name = UUID.randomUUID().toString();
        File file = new File( name );

        // when
        try ( Pipe ignored = new Pipe( name ) )
        {
            assertTrue( file.exists() );
        }

        // then
        assertFalse( file.exists() );
    }
}
