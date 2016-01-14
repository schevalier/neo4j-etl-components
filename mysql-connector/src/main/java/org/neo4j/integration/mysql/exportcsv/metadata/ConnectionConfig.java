package org.neo4j.integration.mysql.exportcsv.metadata;

import java.net.URI;

import org.neo4j.integration.util.Preconditions;

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
