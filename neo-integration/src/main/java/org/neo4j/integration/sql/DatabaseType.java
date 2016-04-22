package org.neo4j.integration.sql;

import java.net.URI;

import static java.lang.String.format;

public enum DatabaseType
{
    MySQL( "com.mysql.jdbc.Driver", 3306 )
            {
                @Override
                public URI createUri( String host, int port, String database )
                {
                    return URI.create(
                            format( "jdbc:mysql://%s:%s/%s?autoReconnect=true&useSSL=false", host, port, database ) );
                }
            };

    private final String driverClassName;
    private final int defaultPort;

    DatabaseType( String driverClassName, int defaultPort )
    {
        this.driverClassName = driverClassName;
        this.defaultPort = defaultPort;
    }

    public String driverClassName()
    {
        return driverClassName;
    }

    public int defaultPort()
    {
        return defaultPort;
    }

    public abstract URI createUri( String host, int port, String database );
}
