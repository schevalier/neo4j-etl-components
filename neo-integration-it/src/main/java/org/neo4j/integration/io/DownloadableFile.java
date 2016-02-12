package org.neo4j.integration.io;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

public class DownloadableFile
{
    private final URI uri;

    public DownloadableFile( URI uri )
    {
        this.uri = uri;
    }

    public ExtractableFile downloadTo( Path destination ) throws IOException
    {
        int connectionTimeout = (int) TimeUnit.SECONDS.toMillis( 10 );
        int readTimeout = (int) TimeUnit.SECONDS.toMillis( 10 );

        String filename = org.neo4j.integration.util.FileUtils.filenameFromUri( uri );
        Path destinationFile = destination.resolve( filename );

        FileUtils.copyURLToFile( uri.toURL(), destinationFile.toFile(), connectionTimeout, readTimeout );

        return new ExtractableFile( destinationFile );
    }
}
