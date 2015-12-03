package org.neo4j.io;

import java.io.InputStream;

public interface StreamRecorder<T>
{
    StreamContentsHandle<T> start( InputStream input );
}
