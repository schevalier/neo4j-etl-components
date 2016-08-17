package org.neo4j.etl.sql;

import java.net.URI;
import java.sql.ResultSet;
import java.sql.Statement;

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

                @Override
                public DatabaseClient.StatementFactory statementFactory()
                {
                    return connection -> {
                        Statement statement = connection.createStatement(
                                ResultSet.TYPE_FORWARD_ONLY,
                                ResultSet.CONCUR_READ_ONLY );
                        statement.setFetchSize( Integer.MIN_VALUE );
                        return statement;
                    };
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

    public abstract DatabaseClient.StatementFactory statementFactory();
}
