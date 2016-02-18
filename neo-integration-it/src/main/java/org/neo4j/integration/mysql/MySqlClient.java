package org.neo4j.integration.mysql;

import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.DatabaseType;

public class MySqlClient
{
    public MySqlClient( String host )
    {
        this.host = host;
    }

    public enum Parameters
    {
        DBRootPassword( "xsjhdcfhsd" ), DBUser( "neo" ), DBPassword( "neo" ), Database( "javabase" );

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

    public void execute( String sql ) throws Exception
    {
        DatabaseClient client = new DatabaseClient( ConnectionConfig.forDatabase( DatabaseType.MySQL )
                .host( host )
                .port( DatabaseType.MySQL.defaultPort() )
                .username( Parameters.DBUser.value() )
                .password( Parameters.DBPassword.value() )
                .build() );

        for ( String line : sql.split( ";" ) )
        {
            if ( !line.trim().isEmpty() )
            {
                client.execute( line ).await();
            }
        }
    }
}
