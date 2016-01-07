package org.neo4j.mysql.config;

import org.neo4j.utils.Preconditions;

public class MySqlConnectionConfig
{
    private final String uri;
    private final String username;
    private final String password;

    public MySqlConnectionConfig( String uri, String username, String password )
    {
        this.uri = Preconditions.requireNonNullString( uri, "Uri" );
        this.username = Preconditions.requireNonNullString( username, "Username" );
        this.password = Preconditions.requireNonNullString( password, "Password" );
    }

    public String uri()
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
