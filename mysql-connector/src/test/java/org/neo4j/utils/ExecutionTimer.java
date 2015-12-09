package org.neo4j.utils;

public class ExecutionTimer
{
    public static ExecutionTimer newTimer()
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
