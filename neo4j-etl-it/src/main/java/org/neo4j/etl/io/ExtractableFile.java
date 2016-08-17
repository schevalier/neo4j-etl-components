package org.neo4j.etl.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import org.neo4j.etl.util.FileUtils;

public class ExtractableFile
{
    private final Path file;

    public ExtractableFile( Path file )
    {
        this.file = file;
    }

    public Stream<Path> extract() throws IOException
    {
        return extractTo( file.getParent() );
    }

    public Stream<Path> extractTo( Path destination ) throws IOException
    {
        if ( Thread.currentThread().isInterrupted() )
        {
            throw new RuntimeException( "Extraction cancelled" );
        }

        Files.createDirectories( destination );

        FileSet beforeExtraction = FileSet.initialFiles( destination );

        if ( isAJar() )
        {
            return FileUtils.files( destination );
        }

        Optional<Archiver> archiver = getArchiver();
        if ( archiver.isPresent() )
        {
            extract( destination, archiver.get() );
            FileSet extractedFiles = beforeExtraction.diff( Collections.<String>emptyList() );
            extractedFiles.makeExecutable();

            return extractedFiles.stream();
        }

        return FileUtils.files( destination );
    }

    private boolean isAJar()
    {
        return file.endsWith( ".jar" );
    }

    private Optional<Archiver> getArchiver()
    {
        try
        {
            return Optional.of( ArchiverFactory.createArchiver( file.toFile() ) );
        }
        catch ( IllegalArgumentException e )
        {
            // The file is not an archive.
            return Optional.empty();
        }
    }

    private void extract( Path destination, Archiver archiver ) throws IOException
    {
        archiver.extract( file.toFile(), destination.toFile() );
    }
}
