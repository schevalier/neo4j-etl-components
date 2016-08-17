package org.neo4j.etl.io;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.neo4j.etl.util.FileUtils;

public class FileSet implements Iterable<Path>
{
    public static FileSet initialFiles( Path directory ) throws IOException
    {
        return new FileSet( directory );
    }

    private final Path directory;
    private final List<Path> initialFiles;
    private final List<Path> diff;

    private FileSet( Path directory ) throws IOException
    {
        this( directory, FileUtils.files( directory ).collect( Collectors.toList() ), Collections.<Path>emptyList() );
    }

    private FileSet( Path directory, List<Path> initialFiles, List<Path> diff )
    {
        this.directory = directory;
        this.initialFiles = initialFiles;
        this.diff = diff;
    }

    public FileSet diff( List<String> filteredFiles ) throws IOException
    {
        List<Path> newInitialFiles = FileUtils.files( directory ).collect( Collectors.toList() );
        List<Path> diffFiles = new ArrayList<>();

        diffFiles.addAll( newInitialFiles );
        diffFiles.removeAll( initialFiles );

        List<Path> filesToRemove = new ArrayList<>();

        for ( Path file : diffFiles )
        {
            if ( Files.isHidden( file ) || IgnoredFiles.isIgnoredFile( file ) )
            {
                filesToRemove.add( file );
            }
            else
            {
                for ( String filteredFile : filteredFiles )
                {
                    if ( file.getFileName().toString().equals( filteredFile ) )
                    {
                        filesToRemove.add( file );
                    }
                }
            }
        }

        diffFiles.removeAll( filesToRemove );

        return new FileSet( directory, newInitialFiles, diffFiles );
    }

    public Stream<Path> stream()
    {
        return diff.stream();
    }

    @Override
    public Iterator<Path> iterator()
    {
        return diff.iterator();
    }

    public void makeExecutable() throws IOException
    {
        for ( Path file : diff )
        {
            java.nio.file.Files.walkFileTree( file, new MakeExecutable() );
        }
    }

    private static class MakeExecutable extends SimpleFileVisitor<Path>
    {
        @Override
        public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException
        {
            //noinspection ResultOfMethodCallIgnored
            file.toFile().setExecutable( true );
            return FileVisitResult.CONTINUE;
        }
    }

    private static class IgnoredFiles
    {
        public static boolean isIgnoredFile( Path file )
        {
            return file.getFileName().toString().contains( "__MACOSX" );
        }
    }
}
