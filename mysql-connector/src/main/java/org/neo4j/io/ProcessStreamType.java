package org.neo4j.io;

public interface ProcessStreamType
{
    void configure( ProcessBuilder processBuilder, StreamRecorder streamRecorder );

    StreamContents start( Process process, StreamRecorder streamRecorder );
}
