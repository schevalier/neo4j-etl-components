package org.neo4j.etl.util;

public class Partial<T>
{
    public final T result;
    public final String remainder;

    public Partial( T result, String remainder )
    {
        this.result = result;
        this.remainder = remainder;
    }
}
