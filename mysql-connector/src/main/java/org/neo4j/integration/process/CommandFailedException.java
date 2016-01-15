package org.neo4j.integration.process;

public class CommandFailedException extends Exception
{
    public CommandFailedException( String message )
    {
        super( message );
    }

    public CommandFailedException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
