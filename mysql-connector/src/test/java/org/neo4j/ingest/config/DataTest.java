package org.neo4j.ingest.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DataTest
{
    @Test
    public void shouldThrowExceptionIfNoFieldNameForPrimitiveType()
    {
        // given
        Data data = new Data( DataType.Int );

        try
        {
            // when
            data.validate( false );
            fail( "Expected IllegalStateException" );
        }
        catch ( IllegalStateException e )
        {
            // then
            assertEquals( e.getMessage(), "Name missing from field of type [int]" );
        }
    }

    @Test
    public void shouldThrowExceptionIfNoFieldNameForArrayType()
    {
        // given
        Data data = new Data( DataType.Int, true );

        try
        {
            // when
            data.validate( false );
            fail( "Expected IllegalStateException" );
        }
        catch ( IllegalStateException e )
        {
            // then
            assertEquals( e.getMessage(), "Name missing from field of type [array of int]" );
        }
    }
}
