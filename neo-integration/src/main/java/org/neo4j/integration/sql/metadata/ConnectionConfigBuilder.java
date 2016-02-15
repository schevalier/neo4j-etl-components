package org.neo4j.integration.sql.metadata;

import java.net.URI;

class ConnectionConfigBuilder implements ConnectionConfig.Builder.SetUri,
        ConnectionConfig.Builder.SetUsername,
        ConnectionConfig.Builder.SetPassword,
        ConnectionConfig.Builder

{
    final String driverClassName;
    URI uri;
    String username;
    String password;

    public ConnectionConfigBuilder( String driverClassName )
    {
        this.driverClassName = driverClassName;
    }

    @Override
    public ConnectionConfig.Builder.SetUsername uri( URI uri )
    {
        this.uri = uri;
        return this;
    }

    @Override
    public ConnectionConfig.Builder.SetPassword username( String username )
    {
        this.username = username;
        return this;
    }

    @Override
    public ConnectionConfig.Builder password( String password )
    {
        this.password = password;
        return this;
    }

    @Override
    public ConnectionConfig build()
    {
        return new ConnectionConfig( this );
    }
}
