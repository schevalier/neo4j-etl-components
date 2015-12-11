package org.neo4j.mysql;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;

import org.neo4j.utils.ResourceRule;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import static org.neo4j.utils.TemporaryDirectory.temporaryDirectory;
import static org.neo4j.utils.TemporaryFile.temporaryFile;

public class AsyncFileOpenerTest
{
    @Rule
    public final ResourceRule<File> tempDirectory = new ResourceRule<>( temporaryDirectory() );

    @Rule
    public final ResourceRule<File> tempFile = new ResourceRule<>( temporaryFile() );

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
