package org.neo4j.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.utils.ResourceRule;
import org.neo4j.utils.StringListBuilder;

import static org.junit.Assert.assertEquals;

import static org.neo4j.utils.TemporaryDirectory.temporaryDirectory;

public class FileDigestTest
{
    @Rule
    public final ResourceRule<Path> tempDirectory = new ResourceRule<>( temporaryDirectory() );

    @Test
    public void shouldReturnFullContentsIfNumberOfLinesDoesNotExceedSummaryLineCount() throws IOException
    {
        // given
        Path file = tempDirectory.get().resolve( "stream-contents" );

        Files.write( file, lines( 4 ) );

        // when
        String value = new FileDigest( file, 6 ).toString();

        // then
        String expectedValue = "line 1" + System.lineSeparator() +
                "line 2" + System.lineSeparator() +
                "line 3" + System.lineSeparator() +
                "line 4";

        assertEquals( expectedValue, value );
    }

    @Test
    public void shouldReturnSummaryContentsIfNumberOfLinesDoesExceedsSummaryLineCount() throws IOException
    {
        // given
        Path file = tempDirectory.get().resolve( "stream-contents" );

        Files.write( file, lines( 8 ) );

        // when
        String value = new FileDigest( file, 6 ).toString();

        // then
        String expectedValue = "line 1" + System.lineSeparator() +
                "line 2" + System.lineSeparator() +
                "line 3" + System.lineSeparator() +
                "..." + System.lineSeparator() +
                "line 6" + System.lineSeparator() +
                "line 7" + System.lineSeparator() +
                "line 8";

        assertEquals( expectedValue, value );
    }

    private byte[] lines( int size )
    {
        List<String> lines = new ArrayList<>();
        for ( int i = 1; i <= size; i++ )
        {
            lines.add( "line " + i );
        }
        return StringListBuilder.stringList( lines, System.lineSeparator() ).toString().getBytes();
    }
}
