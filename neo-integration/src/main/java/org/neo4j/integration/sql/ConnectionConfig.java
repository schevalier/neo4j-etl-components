package org.neo4j.integration.sql;

import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.neo4j.integration.util.Preconditions;

public class ConnectionConfig
{
    public static Builder.SetHost forDatabase( DatabaseType databaseType )
    {
        return new ConnectionConfigBuilder( databaseType );
    }

    public static ConnectionConfig fromJson(JsonNode root, Credentials credentials)
    {
        DatabaseType databaseType = DatabaseType.valueOf( root.path( "database-type" ).textValue() );

        return forDatabase( databaseType )
                .host( root.path( "host" ).textValue() )
                .port( root.path( "port" ).intValue() )
                .database( root.path( "database" ).textValue() )
                .username( credentials.username() )
                .password( credentials.password() )
                .build();
    }

    private final DatabaseType databaseType;
    private final String host;
    private final int port;
    private final String database;
    private final Credentials credentials;

    ConnectionConfig( ConnectionConfigBuilder builder )
    {
        this.databaseType = Preconditions.requireNonNull( builder.databaseType, "DatabaseType" );
        this.host = Preconditions.requireNonNullString( builder.host, "Host" );
        this.port = builder.port;
        this.database = Preconditions.requireNonNull( builder.database, "Database" );
        this.credentials = new Credentials(
                Preconditions.requireNonNullString( builder.username, "Username" ),
                Preconditions.requireNonNullString( builder.password, "Password" ) );
    }

    public String driverClassName()
    {
        return databaseType.driverClassName();
    }

    public URI uri()
    {
        return databaseType.createUri( host, port, database );
    }

    public Credentials credentials()
    {
        return credentials;
    }

    public JsonNode toJson()
    {
        ObjectNode root = JsonNodeFactory.instance.objectNode();

        root.put( "database-type", databaseType.name());
        root.put( "host", host );
        root.put( "port", port );
        root.put( "database", database );

        return root;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals( Object o )
    {
        return EqualsBuilder.reflectionEquals( this, o );
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode( this );
    }

    public interface Builder
    {
        interface SetHost
        {
            SetPort host( String host );
        }

        interface SetPort
        {
            SetDatabaseOrUsername port( int port );
        }

        interface SetDatabaseOrUsername
        {
            SetUsername database( String database );

            SetPassword username( String username );
        }

        interface SetUsername
        {
            SetPassword username( String username );
        }

        interface SetPassword
        {
            Builder password( String password );
        }

        ConnectionConfig build();
    }
}
