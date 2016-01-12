package org.neo4j.mysql.config;

import java.net.URI;

import org.neo4j.utils.Preconditions;

public class ConnectionConfig
{
    private final URI uri;
    private final String username;
    private final String password;

    public ConnectionConfig( URI uri, String username, String password )
    {
        this.uri = Preconditions.requireNonNull( uri, "Uri" );
        this.username = Preconditions.requireNonNullString( username, "Username" );
        this.password = Preconditions.requireNonNullString( password, "Password" );
    }

    public URI uri()
    {
        return uri;
    }

    public String username()
    {
        return username;
    }

    public String password()
    {
        return password;
    }
}
