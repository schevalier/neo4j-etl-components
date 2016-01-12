package org.neo4j.integration.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.neo4j.integration.cli.Commands;
import org.neo4j.integration.util.FutureUtils;
import org.neo4j.integration.util.OperatingSystem;

public class Pipe implements AutoCloseable
{
    private final ExecutorService executor = Executors.newFixedThreadPool( 3 );
    private final Path file;

    public Pipe( String name ) throws Exception
    {
        if ( OperatingSystem.isWindows() )
        {
            throw new IllegalStateException( "Not supported in Windows" );
        }

        this.file = Paths.get( name ).toAbsolutePath();

        FifoFactory.Instance.create( file );
    }

    public CompletableFuture<InputStream> in( CompletableFuture<?>... exceptionables )
    {
        return FutureUtils.failFastFuture( executor, createInputStream(), exceptionables );
    }

    public CompletableFuture<OutputStream> out( CompletableFuture<?>... exceptionables )
    {
        return FutureUtils.failFastFuture( executor, createOutputStream(), exceptionables );
    }

    @Override
    public void close() throws IOException
    {
        Files.deleteIfExists( file );
        executor.shutdownNow();
    }

    public String name()
    {
        return file.toAbsolutePath().toString();
    }

    private CompletableFuture<InputStream> createInputStream()
    {
        return FutureUtils.exceptionableFuture( () -> Files.newInputStream( file ), executor );
    }

    private CompletableFuture<OutputStream> createOutputStream()
    {
        return FutureUtils.exceptionableFuture( () -> Files.newOutputStream( file ), executor );
    }

    private enum FifoFactory
    {
        Instance;

        public synchronized void create( Path file ) throws Exception
        {
            if ( Files.notExists( file ) )
            {
                String filePath = file.toString();
                Commands.commands( "mkfifo", filePath ).execute().await();
                Commands.commands( "chmod", "666", filePath ).execute().await();
            }
        }
    }
}
