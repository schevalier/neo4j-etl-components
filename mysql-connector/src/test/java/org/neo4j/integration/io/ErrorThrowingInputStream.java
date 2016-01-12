package org.neo4j.integration.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ErrorThrowingInputStream extends FilterInputStream
{
    private final IOException ex;
    private final int numberOfGoodBytes;
    private int byteCount = 0;

    public ErrorThrowingInputStream( InputStream in, IOException ex )
    {
        this( in, ex, 0 );
    }

    public ErrorThrowingInputStream( InputStream in, IOException ex, int numberOfGoodBytes )
    {
        super( in );
        this.ex = ex;
        this.numberOfGoodBytes = numberOfGoodBytes;
    }

    @Override
    public int read( @SuppressWarnings("NullableProblems") byte[] b, int off, int len ) throws IOException
    {
        int read = super.read( b, off, len );
        byteCount += read;

        if ( byteCount > numberOfGoodBytes )
        {
            throw ex;
        }

        return read;
    }
}
