package org.neo4j.integration.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TemporaryFile
{
    public static Resource<Path> temporaryFile()
    {
        return temporaryFile( "tmp", "file" );
    }

    public static Resource<Path> temporaryFile( String prefix, String suffix )
    {
        return new LazyResource<>( new LazyResource.Lifecycle<Path>()
        {
            @Override
            public Path create() throws IOException
            {
                return Files.createTempFile( prefix, suffix );
            }

            @Override
            public void destroy( Path file ) throws IOException
            {
                try
                {
                    Files.deleteIfExists( file );
                }
                catch ( IOException e )
                {
                    // Retry
                    try
                    {
                        Files.deleteIfExists( file );
                    }
                    catch ( Exception ex )
                    {
                        ex.printStackTrace();
                    }
                }
            }
        } );
    }
}
