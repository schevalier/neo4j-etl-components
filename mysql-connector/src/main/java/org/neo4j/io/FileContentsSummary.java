package org.neo4j.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.input.ReversedLinesFileReader;

import org.neo4j.utils.StringListBuilder;

class FileContentsSummary implements StreamContents
{
    private static final int DEFAULT_SUMMARY_LINE_COUNT = 50;

    private final File file;
    private final int summaryLineCount;

    public FileContentsSummary( File file )
    {
        this( file, DEFAULT_SUMMARY_LINE_COUNT );
    }

    public FileContentsSummary( File file, int summaryLineCount )
    {
        if ( summaryLineCount < 1 )
        {
            throw new IllegalArgumentException( "summaryLineCount must be greater than zero" );
        }

        this.file = file;
        this.summaryLineCount = summaryLineCount;
    }

    @Override
    public Optional<File> file()
    {
        return Optional.of( file );
    }

    @Override
    public String value()
    {
        List<String> lines = new ArrayList<>();
        List<String> endLines = new ArrayList<>();

        int lineCount = 0;

        try
        {
            int numberOfLinesToReadFromStartOfFile = summaryLineCount / 2;

            try ( BufferedReader input = new BufferedReader( new FileReader( file ) ) )
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
                try ( ReversedLinesFileReader reader = new ReversedLinesFileReader( file ) )
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

