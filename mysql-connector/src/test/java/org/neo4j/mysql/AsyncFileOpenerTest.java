package org.neo4j.mysql;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AsyncFileOpenerTest
{
    @Test
    public void shouldRethrowDependencyExceptions() throws Exception
    {
        // given
        IOException expectedException = new IOException( "Reader IO exception" );

        Exceptions exceptions = () -> {
            throw expectedException;
        };

        AsyncFileOpener fileOpener = new AsyncFileOpener( new File( "pipe" ), 1000, exceptions );

        try
        {
            // when
            fileOpener.open();
            fail( "Expected IOException" );
        }
        catch ( IOException e )
        {
            // then
            assertEquals( e, expectedException );
        }
    }
}
