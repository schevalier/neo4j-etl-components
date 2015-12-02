package org.neo4j.io;

import java.io.InputStream;

public interface StreamRecorder
{
    StreamContents start( InputStream input );
}
