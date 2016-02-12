package org.neo4j.integration.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class FileUtils
{
    public static String filenameFromUri( URI uri )
    {
        return new File( uri.getPath() ).getName();
    }

    public static Stream<Path> files( Path directory ) throws IOException
    {
        File[] files = directory.toFile().listFiles();

        return files == null ? Stream.<Path>empty() : asList( files ).stream().map( File::toPath );
    }
}
