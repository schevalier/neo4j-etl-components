package org.neo4j.mysql;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.utils.ResourceRule;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import static org.neo4j.utils.TemporaryDirectory.temporaryDirectory;

public class AsyncFileOpenerTest
{
    @Rule
    public final ResourceRule<File> tempDirectory = new ResourceRule<>( temporaryDirectory() );

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

    @Test
    public void shouldThrowExceptionIfCreatingOutputStreamFails() throws Exception
    {
        try
        {
            // when
            new AsyncFileOpener( tempDirectory.get(), 1000, mock( Exceptions.class ) ).open();
            fail( "Expected IOException" );
        }
        catch ( IOException e )
        {
            // then
            assertThat( e, instanceOf( FileNotFoundException.class ) );
        }
    }
}
