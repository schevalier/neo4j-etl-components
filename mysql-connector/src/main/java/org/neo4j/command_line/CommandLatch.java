package org.neo4j.command_line;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.neo4j.io.InMemoryStreamRecorder;
import org.neo4j.io.StreamEventHandler;
import org.neo4j.io.StreamEventLatch;

public class CommandLatch implements StreamEventHandler<CommandLatch.CommandLatchResult>
{
    private final CommandPredicate predicate;
    private final InMemoryStreamRecorder streamRecorder = new InMemoryStreamRecorder();
    private final StreamEventLatch streamEventLatch = new StreamEventLatch();

    public CommandLatch( CommandPredicate predicate )
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
        catch ( IOException e )
        {
            onException( e );
            onCompleted();
        }
    }

    @Override
    public void onException( IOException e )
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
    public CommandLatchResult awaitContents( long timeout, TimeUnit unit ) throws IOException
    {
        boolean ok = streamEventLatch.awaitContents( timeout, unit );
        String streamContents = streamRecorder.awaitContents( 0, TimeUnit.MILLISECONDS );

        return new CommandLatchResult( ok, streamContents );
    }

    public interface CommandPredicate
    {
        boolean test( String line ) throws IOException;
    }

    public static class CommandLatchResult
    {
        private final boolean ok;
        private final String streamContents;

        public CommandLatchResult( boolean ok, String streamContents )
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
