package org.neo4j.io;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public interface StreamEventHandler<T>
{
    void onLine( String line ) throws IOException;

    void onException( IOException e );

    void onCompleted() throws IOException;

    T awaitContents(long timeout, TimeUnit unit) throws IOException;
}
