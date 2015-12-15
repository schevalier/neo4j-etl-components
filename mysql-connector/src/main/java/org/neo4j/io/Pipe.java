package org.neo4j.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

import org.neo4j.command_line.Commands;
import org.neo4j.utils.FutureUtils;
import org.neo4j.utils.OperatingSystem;

public class Pipe implements AutoCloseable
{
    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private final File file;
    private final int bufferSize;

    public Pipe( String name ) throws Exception
    {
        this( name, DEFAULT_BUFFER_SIZE );
    }

    public Pipe( String name, int bufferSize ) throws Exception
    {
        if ( OperatingSystem.isWindows())
        {
            throw new IllegalStateException( "Not supported in Windows" );
        }

        this.file = new File( name );
        this.bufferSize = bufferSize;

        FifoFactory.Instance.create( file );
    }

    public CompletableFuture<InputStream> in( CompletableFuture<?>... exceptionables )
    {
        return FutureUtils.failFastFuture( createInputStream(), exceptionables );
    }

    public CompletableFuture<OutputStream> out( CompletableFuture<?>... exceptionables )
    {
        return FutureUtils.failFastFuture( createOutputStream(), exceptionables );
    }

    @Override
    public void close() throws IOException
    {
        Files.deleteIfExists( file.toPath() );
    }

    public String name()
    {
        return file.getAbsolutePath();
    }

    private CompletableFuture<InputStream> createInputStream()
    {
        return FutureUtils.exceptionableFuture(
                () -> new BufferedInputStream( new FileInputStream( file ), bufferSize ) );
    }

    private CompletableFuture<OutputStream> createOutputStream()
    {
        return FutureUtils.exceptionableFuture(
                () -> new BufferedOutputStream( new FileOutputStream( file ), bufferSize ) );
    }

    private enum FifoFactory
    {
        Instance;

        public synchronized void create( File file ) throws Exception
        {
            if ( !file.exists() )
            {
                Commands.commands( "mkfifo", file.getAbsolutePath() ).execute().await();
                Commands.commands( "chmod", "666", file.getAbsolutePath() ).execute().await();
            }
        }
    }
}
