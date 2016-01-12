package org.neo4j.ingest.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DataTest
{
    @Test
    public void shouldThrowExceptionIfNullNameForPrimitiveType()
    {
        try
        {
            // when
            new Data( null, DataType.Int );
            fail( "Expected NullPointerException" );
        }
        catch ( NullPointerException e )
        {
            // then
            assertEquals( e.getMessage(), "Name cannot be null" );
        }
    }

    @Test
    public void shouldThrowExceptionIfNullNameForArrayType()
    {
        try
        {
            // when
            new Data( null, DataType.Int, true );
            fail( "Expected NullPointerException" );
        }
        catch ( NullPointerException e )
        {
            // then
            assertEquals( e.getMessage(), "Name cannot be null" );
        }
    }

    @Test
    public void shouldThrowExceptionIfEmptyStringNameForPrimitiveType()
    {
        try
        {
            // when
            new Data( " \t", DataType.Int );
            fail( "Expected IllegalArgumentException" );
        }
        catch ( IllegalArgumentException e )
        {
            // then
            assertEquals( e.getMessage(), "Name cannot be empty" );
        }
    }

    @Test
    public void shouldThrowExceptionIfEmptyStringlNameForArrayType()
    {
        try
        {
            // when
            new Data( " \t", DataType.Int, true );
            fail( "Expected IllegalArgumentException" );
        }
        catch ( IllegalArgumentException e )
        {
            // then
            assertEquals( e.getMessage(), "Name cannot be empty" );
        }
    }
}
