package org.neo4j.integration.util;

public class ExecutionTimer
{
    public static ExecutionTimer startTimer()
    {
        return new ExecutionTimer( System.currentTimeMillis() );
    }

    private final long startTimeMillis;

    ExecutionTimer( long startTimeMillis )
    {
        this.startTimeMillis = startTimeMillis;
    }

    public long duration()
    {
        return System.currentTimeMillis() - startTimeMillis;
    }
}
