package org.neo4j.mysql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    public void shouldDeleteFileOnClose() throws Exception
    {
        // given
        String name = UUID.randomUUID().toString();
        File file = new File( name );

        // when
        try ( Pipe ignored = new Pipe( name ))
        {
            assertTrue( file.exists() );
        }

        // then
        assertFalse( file.exists() );
    }
}
