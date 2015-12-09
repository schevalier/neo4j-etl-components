package org.neo4j.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;

public class TemporaryDirectory
{
    public static Resource<File> temporaryDirectory()
    {
        return temporaryDirectory( "tmp" );
    }

    public static Resource<File> temporaryDirectory( String prefix )
    {
        return new LazyResource<>( new LazyResource.Lifecycle<File>()
        {
            @Override
            public File create() throws IOException
            {
                return Files.createTempDirectory( prefix ).toFile();
            }

            @Override
            public void destroy( File file ) throws IOException
            {
                try
                {
                    FileUtils.deleteDirectory( file );

                }
                catch ( IOException e )
                {
                    // Retry
                    try
                    {
                        FileUtils.deleteDirectory( file );
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
