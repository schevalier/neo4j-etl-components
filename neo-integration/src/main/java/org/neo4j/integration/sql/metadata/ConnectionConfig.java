package org.neo4j.integration.sql.metadata;

import java.net.URI;

import org.neo4j.integration.util.Preconditions;

public class ConnectionConfig
{
    public static Builder.SetUri forMySql()
    {
        return new ConnectionConfigBuilder("com.mysql.jdbc.Driver");
    }

    private final String driverClassName;
    private final URI uri;
    private final String username;
    private final String password;

    ConnectionConfig( ConnectionConfigBuilder builder )
    {
        this.driverClassName = Preconditions.requireNonNullString( builder.driverClassName, "Driver class name" );
        this.uri = Preconditions.requireNonNull( builder.uri, "Uri" );
        this.username = Preconditions.requireNonNullString( builder.username, "Username" );
        this.password = Preconditions.requireNonNullString( builder.password, "Password" );
    }

    public String driverClassName()
    {
        return driverClassName;
    }

    public URI getUri()
    {
        return uri;
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

    public interface Builder
    {
        interface SetUri
        {
            SetUsername uri(URI uri);
        }

        interface SetUsername
        {
            SetPassword username(String username);
        }

        interface SetPassword
        {
            Builder password(String password);
        }

        ConnectionConfig build();
    }
}
