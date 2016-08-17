package org.neo4j.etl.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.etl.util.ResourceRule;

import static org.junit.Assert.assertEquals;

import static org.neo4j.etl.util.TemporaryDirectory.temporaryDirectory;

public class FileDigestTest
{
    private static final String NEWLINE = System.lineSeparator();

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
        String expectedValue = "line 1" + NEWLINE +
                "line 2" + NEWLINE +
                "line 3" + NEWLINE +
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
        String expectedValue = "line 1" + NEWLINE +
                "line 2" + NEWLINE +
                "line 3" + NEWLINE +
                "..." + NEWLINE +
                "line 6" + NEWLINE +
                "line 7" + NEWLINE +
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
        return lines.stream().collect( Collectors.joining( NEWLINE ) ).getBytes();
    }
}
