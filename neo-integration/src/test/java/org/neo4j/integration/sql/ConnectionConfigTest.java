package org.neo4j.integration.sql;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConnectionConfigTest
{
    @Test
    public void shouldSerializeToAndDeserializeFromJson()
    {
        // given
        Credentials credentials = new Credentials( "user-a", "passowrd" );
        ConnectionConfig config = ConnectionConfig.forDatabase( DatabaseType.MySQL )
                .host( "localhost" )
                .port( 9696 )
                .database( "test-db" )
                .username( credentials.username() )
                .password( credentials.password() )
                .build();

        JsonNode json = config.toJson();

        // when
        ConnectionConfig deserialized = ConnectionConfig.fromJson( json, credentials );

        // then
        assertEquals( config, deserialized );
    }
}
