package org.neo4j.integration.mysql;

import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.DatabaseType;

public class MySqlClient
{
    public MySqlClient( String host )
    {
        this( host, Parameters.DBUser.value(), Parameters.DBPassword.value() );
    }

    public MySqlClient( String host, String user, String password )
    {
        this.host = host;
        this.user = user;
        this.password = password;
    }

    public enum Parameters
    {
        DBRootUser( "root" ), DBRootPassword( "xsjhdcfhsd" ), DBUser( "neo" ), DBPassword( "neo" );

        private final String value;

        Parameters( String value )
        {
            this.value = value;
        }

        public String value()
        {
            return value;
        }
    }

    private final String host;
    private final String user;
    private final String password;

    public void execute( String sql ) throws Exception
    {
        DatabaseClient client = new DatabaseClient(
                ConnectionConfig.forDatabase( DatabaseType.MySQL )
                        .host( host )
                        .port( DatabaseType.MySQL.defaultPort() )
                        .username( user )
                        .password( password )
                        .build(), "" );

        for ( String line : sql.split( ";" ) )
        {
            if ( !line.trim().isEmpty() )
            {
                client.execute( line ).await();
            }
        }
    }
}
