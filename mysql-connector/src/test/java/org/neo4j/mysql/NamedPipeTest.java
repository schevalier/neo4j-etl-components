package org.neo4j.mysql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NamedPipeTest
{
    private static final String NEWLINE = System.lineSeparator();

    @Test
    public void shouldCreateNamedPipeForWriting() throws Exception
    {
        // given
        String expectedResults = "line 1" + NEWLINE + "line 2" + NEWLINE;
        StringBuilder results = new StringBuilder();

        String name = UUID.randomUUID().toString();
        FileBasedPipeReader reader = new FileBasedPipeReader( new File( name ) );

        // when
        try ( NamedPipe pipe = new NamedPipe( name, reader );
              Writer writer = new OutputStreamWriter( pipe.open() ) )
        {
            writer.write( "line 1" );
            writer.write( NEWLINE );
            writer.write( "line 2" );
            writer.write( NEWLINE );
            writer.write( NEWLINE );
            writer.flush();

            String line;

            while ( (line = reader.reader().readLine()) != null && !line.equals( "" ) )
            {
                results.append( line ).append( NEWLINE );
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
        try ( NamedPipe pipe = new NamedPipe( name, new FileBasedPipeReader( file ) );
              OutputStream ignored = pipe.open() )
        {
            assertTrue( file.exists() );
        }

        // then
        assertFalse( file.exists() );
    }

    private static class FileBasedPipeReader implements PipeReader
    {
        private final File file;
        private volatile BufferedReader reader;
        private volatile Exception ex;

        private FileBasedPipeReader( File file )
        {
            this.file = file;
        }

        @Override
        public void open()
        {
            try
            {
                reader = new BufferedReader( new FileReader( file.getAbsoluteFile() ) );
            }
            catch ( FileNotFoundException e )
            {
                ex = e;
            }
        }

        @Override
        public void close() throws Exception
        {
            if ( reader != null )
            {
                reader.close();
            }
        }

        @Override
        public void rethrow() throws Exception
        {
            if ( ex != null )
            {
                throw ex;
            }
        }

        public BufferedReader reader()
        {
            return reader;
        }
    }
}
