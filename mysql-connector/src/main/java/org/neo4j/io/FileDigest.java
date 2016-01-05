package org.neo4j.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.input.ReversedLinesFileReader;

import org.neo4j.utils.StringListBuilder;

public class FileDigest
{
    private static final int DEFAULT_SUMMARY_LINE_COUNT = 50;

    private final Path file;
    private final int summaryLineCount;

    FileDigest( Path file )
    {
        this( file, DEFAULT_SUMMARY_LINE_COUNT );
    }

    FileDigest( Path file, int summaryLineCount )
    {
        if ( summaryLineCount < 1 )
        {
            throw new IllegalArgumentException( "summaryLineCount must be greater than zero" );
        }

        this.file = file;
        this.summaryLineCount = summaryLineCount;
    }

    public Path file()
    {
        return file;
    }

    @Override
    public String toString()
    {
        return value();
    }

    private String value()
    {
        List<String> lines = new ArrayList<>();
        List<String> endLines = new ArrayList<>();

        int lineCount = 0;

        try
        {
            int numberOfLinesToReadFromStartOfFile = summaryLineCount / 2;

            try ( BufferedReader input = Files.newBufferedReader( file ) )
            {
                String line;

                while ( ((line = input.readLine()) != null) && lineCount <= summaryLineCount )
                {
                    if ( lineCount < numberOfLinesToReadFromStartOfFile )
                    {
                        lines.add( line );
                    }
                    lineCount++;
                }
            }

            boolean isSummary = lineCount - summaryLineCount > 0;

            if ( isSummary )
            {
                lines.add( "..." );
            }

            int numberOfLinesToReadFromEndOfFile = isSummary ?
                    lineCount - numberOfLinesToReadFromStartOfFile - 1 :
                    lineCount - numberOfLinesToReadFromStartOfFile;

            if ( numberOfLinesToReadFromEndOfFile > 0 )
            {
                try ( ReversedLinesFileReader reader = new ReversedLinesFileReader( file.toFile() ) )
                {
                    for ( int i = 0; i < numberOfLinesToReadFromEndOfFile; i++ )
                    {
                        endLines.add( reader.readLine() );
                    }
                }
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }

        Collections.reverse( endLines );
        lines.addAll( endLines );

        return StringListBuilder.stringList( lines, System.lineSeparator() ).toString();
    }
}

