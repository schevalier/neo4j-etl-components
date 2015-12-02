package org.neo4j.command_line;

import org.neo4j.io.ProcessStreamType;
import org.neo4j.io.StreamContents;
import org.neo4j.io.StreamRecorder;

class ProcessConfigurator
{
    private final StreamRecorder streamRecorder;
    private final ProcessStreamType processStreamType;

    public ProcessConfigurator( StreamRecorder streamRecorder, ProcessStreamType processStreamType )
    {
        this.streamRecorder = streamRecorder;
        this.processStreamType = processStreamType;
    }

    public void configure( ProcessBuilder processBuilder )
    {
        processStreamType.configure( processBuilder, streamRecorder );
    }

    public StreamContents start( Process process )
    {
        return processStreamType.start( process, streamRecorder );
    }
}
