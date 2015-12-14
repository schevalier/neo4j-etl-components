package org.neo4j.pipes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import org.neo4j.command_line.Commands;

public class Pipe implements AutoCloseable
{
    private static final int BUFFER_SIZE = 1000;

    private final File file;

    public Pipe( String name )
    {
        this.file = new File( name );
    }

    public CompletableFuture<InputStream> in() throws Exception
    {
        FifoFactory.Instance.create( file );

        CompletableFuture<InputStream> future = new CompletableFuture<>();

        ForkJoinPool.commonPool().submit( () -> {
            try
            {
                future.complete( new BufferedInputStream( new FileInputStream( file ), BUFFER_SIZE ) );
            }
            catch ( Exception ex )
            {
                future.completeExceptionally( ex );
            }
        } );

        return future;
    }

    public CompletableFuture<OutputStream> out() throws Exception
    {
        FifoFactory.Instance.create( file );

        CompletableFuture<OutputStream> future = new CompletableFuture<>();

        ForkJoinPool.commonPool().submit( () -> {
            try
            {
                future.complete( new BufferedOutputStream( new FileOutputStream( file ), BUFFER_SIZE ) );
            }
            catch ( Exception ex )
            {
                future.completeExceptionally( ex );
            }
        } );

        return future;
    }

    @Override
    public void close() throws Exception
    {
        Files.deleteIfExists( file.toPath() );
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
