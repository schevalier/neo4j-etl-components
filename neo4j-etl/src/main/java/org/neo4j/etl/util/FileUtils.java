package org.neo4j.etl.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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

    public static void deleteRecursively( Path start ) throws IOException
    {
        if ( Files.notExists( start ) )
        {
            return;
        }

        Files.walkFileTree( start, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException
            {
                Files.deleteIfExists( file );
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory( Path dir, IOException exc ) throws IOException
            {
                Files.deleteIfExists( dir );
                return FileVisitResult.CONTINUE;
            }
        } );
    }
}
