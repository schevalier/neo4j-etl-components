package org.neo4j.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ErrorThrowingInputStream extends FilterInputStream
{
    private final IOException ex;

    /**
     * Creates a <code>FilterInputStream</code>
     * by assigning the  argument <code>in</code>
     * to the field <code>this.in</code> so as
     * to remember it for later use.
     *
     * @param in the underlying input stream, or <code>null</code> if
     *           this instance is to be created without an underlying stream.
     */
    protected ErrorThrowingInputStream( InputStream in )
    {
        this( in, new IOException( "Stream error" ) );
    }

    public ErrorThrowingInputStream( InputStream in, IOException ex )
    {
        super( in );
        this.ex = ex;
    }

    @Override
    public int read( @SuppressWarnings("NullableProblems") byte[] b, int off, int len ) throws IOException
    {
        throw ex;
    }
}
