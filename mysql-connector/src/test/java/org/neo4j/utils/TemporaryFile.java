package org.neo4j.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;

public class TemporaryFile
{
    public static Resource<File> temporaryFile()
    {
        return temporaryFile( "tmp", "file" );
    }

    public static Resource<File> temporaryFile( String prefix, String suffix )
    {
        return new LazyResource<>( new LazyResource.Lifecycle<File>()
        {
            @Override
            public File create() throws IOException
            {
                return Files.createTempFile( prefix, suffix ).toFile();
            }

            @Override
            public void destroy( File file ) throws IOException
            {
                try
                {
                    FileUtils.forceDelete( file );
                }
                catch ( IOException e )
                {
                    // Retry
                    try
                    {
                        FileUtils.forceDelete( file );
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
