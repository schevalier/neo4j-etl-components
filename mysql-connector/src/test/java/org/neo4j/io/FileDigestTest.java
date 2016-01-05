package org.neo4j.io;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.utils.ResourceRule;

import static org.apache.commons.io.FileUtils.writeLines;
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
        File file = new File( tempDirectory.get().toFile(), "stream-contents" );

        writeLines( file, StandardCharsets.UTF_8.toString(), lines( 4 ) );

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
        File file = new File( tempDirectory.get().toFile(), "stream-contents" );

        writeLines( file, StandardCharsets.UTF_8.toString(), lines( 8 ) );

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

    private Collection<String> lines( int size )
    {
        List<String> lines = new ArrayList<>();
        for ( int i = 1; i <= size; i++ )
        {
            lines.add( "line " + i );
        }
        return lines;
    }
}
