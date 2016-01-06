package org.neo4j.ingest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DataTest
{
    @Test
    public void shouldThrowExceptionIfNoFieldNameForPrimitiveType()
    {
        // given
        Data data = Data.ofType( DataType.Int );

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
        Data data = Data.arrayOfType( DataType.Int );

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
