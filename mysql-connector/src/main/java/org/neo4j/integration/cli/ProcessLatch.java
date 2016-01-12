package org.neo4j.integration.cli;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.neo4j.integration.io.InMemoryStreamRecorder;
import org.neo4j.integration.io.StreamEventHandler;
import org.neo4j.integration.io.StreamEventLatch;

public class ProcessLatch implements StreamEventHandler<ProcessLatch.ProcessLatchResult>
{
    private final StreamPredicate predicate;
    private final InMemoryStreamRecorder streamRecorder = new InMemoryStreamRecorder();
    private final StreamEventLatch streamEventLatch = new StreamEventLatch();

    public ProcessLatch( StreamPredicate predicate )
    {
        this.predicate = predicate;
    }

    @Override
    public void onLine( String line ) throws IOException
    {
        streamRecorder.onLine( line );

        try
        {
            if ( predicate.test( line ) )
            {
                streamEventLatch.onLine( line );
            }
        }
        catch ( Exception e )
        {
            onException( e );
            onCompleted();
        }
    }

    @Override
    public void onException( Exception e )
    {
        streamEventLatch.onException( e );
        streamRecorder.onException( e );
    }

    @Override
    public void onCompleted() throws IOException
    {
        streamRecorder.onCompleted();
        streamEventLatch.onCompleted();
    }

    @Override
    public ProcessLatchResult awaitContents( long timeout, TimeUnit unit ) throws Exception
    {
        boolean ok = streamEventLatch.awaitContents( timeout, unit );
        String streamContents = streamRecorder.awaitContents( 0, TimeUnit.MILLISECONDS );

        return new ProcessLatchResult( ok, streamContents );
    }

    public interface StreamPredicate
    {
        boolean test( String line ) throws Exception;
    }

    public static class ProcessLatchResult
    {
        private final boolean ok;
        private final String streamContents;

        public ProcessLatchResult( boolean ok, String streamContents )
        {
            this.ok = ok;
            this.streamContents = streamContents;
        }

        public boolean ok()
        {
            return ok;
        }

        public String streamContents()
        {
            return streamContents;
        }
    }
}
