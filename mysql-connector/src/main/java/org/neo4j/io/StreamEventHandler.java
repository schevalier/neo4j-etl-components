package org.neo4j.io;

import java.io.IOException;

public interface StreamEventHandler
{
    void onLine( String line ) throws IOException;

    void onException( IOException e );

    void onCompleted() throws IOException;
}
