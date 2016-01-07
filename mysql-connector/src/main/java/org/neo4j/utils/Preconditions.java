package org.neo4j.utils;

public class Preconditions
{
    public static String requireNonNullString(String value, String message)
    {
        if (value == null)
        {
            throw new NullPointerException( message );
        }

        if (value.trim().isEmpty())
        {
            throw new IllegalArgumentException( message );
        }

        return value;
    }
}
