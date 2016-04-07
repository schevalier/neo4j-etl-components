package org.neo4j.integration.neo4j.importcsv.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FormattingTest
{
    @Test
    public void shouldSerializeToAndDeserializeFromJson()
    {
        // given
        Formatting original = Formatting.DEFAULT;
        JsonNode json = original.toJson();

        // when
        Formatting deserialized = Formatting.fromJson( json );

        // then
        assertEquals( original.delimiter(), deserialized.delimiter() );
        assertEquals( original.arrayDelimiter(), deserialized.arrayDelimiter() );
        assertEquals( original.quote(), deserialized.quote() );
        assertEquals( original.labelFormatter().getClass(), deserialized.labelFormatter().getClass() );
        assertEquals( original.relationshipFormatter().getClass(), deserialized.relationshipFormatter().getClass() );
        assertEquals( original.propertyFormatter().getClass(), deserialized.propertyFormatter().getClass() );
    }

    @Test
    public void shouldThrowExceptionIfUnableToCreateFormatter()
    {
        // given
        Formatting original = Formatting.DEFAULT;
        JsonNode json = original.toJson();

        ((ObjectNode) json).put( "label-formatter", "unknown.label.formatter" );

        try
        {
            // when
            Formatting.fromJson( json );
            fail( "Expected IllegalStateException" );
        }
        catch ( IllegalStateException e )
        {
            // then
            assertEquals( "Unable to create formatter 'unknown.label.formatter'", e.getMessage() );
        }
    }
}
