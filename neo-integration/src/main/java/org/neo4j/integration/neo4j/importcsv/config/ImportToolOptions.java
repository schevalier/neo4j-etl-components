package org.neo4j.integration.neo4j.importcsv.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import org.neo4j.integration.process.Commands;
import org.neo4j.integration.util.Loggers;

import static java.lang.String.format;

public class ImportToolOptions
{
    private final Map<String, String> importToolOptions;

    public ImportToolOptions( Map<String, String> importToolOptions )
    {
        this.importToolOptions = importToolOptions;
    }

    Map<String, String> options()
    {
        return importToolOptions;
    }

    public static ImportToolOptions initialiseFromFile( Path optionsFile )
    {
        ObjectMapper objectMapper = new ObjectMapper();

        if ( Files.exists( optionsFile ) && !Files.isDirectory( optionsFile ) )
        {
            File file = optionsFile.toFile();
            try
            {
                @SuppressWarnings("unchecked")
                Map<String, String> options = objectMapper.readValue( file, HashMap.class );
                return new ImportToolOptions( options );
            }
            catch ( IOException e )
            {
                Loggers.Default.log( Level.WARNING,
                        format( "Skipping reading options from file %s due to error.", optionsFile ), e );
                return new ImportToolOptions( Collections.emptyMap() );
            }
        }
        else
        {
            Loggers.Default.log( Level.INFO,
                    format( "Skipping reading import options from file because file [%s] doesn't exist.",
                            optionsFile ) );
            return new ImportToolOptions( Collections.emptyMap() );
        }
    }

    public Delimiter getDelimiter( String delimiter )
    {
        if ( StringUtils.isNotEmpty( delimiter ) )
        {
            return new Delimiter( delimiter );
        }
        else if ( importToolOptions.containsKey( "delimiter" ) )
        {
            return new Delimiter( importToolOptions.get( "delimiter" ) );
        }
        else
        {
            return Formatting.DEFAULT_DELIMITER;
        }
    }

    public QuoteChar getQuoteCharacter( String quote )
    {
        if ( StringUtils.isNotEmpty( quote ) )
        {
            return new QuoteChar( quote, quote );
        }
        else if ( importToolOptions.containsKey( "quote" ) )
        {
            return new QuoteChar( importToolOptions.get( "quote" ), importToolOptions.get( "quote" ) );
        }
        else
        {
            return Formatting.DEFAULT_QUOTE_CHAR;
        }
    }

    public void addOptionsAsCommands( Commands.Builder.SetCommands commands )
    {
        importToolOptions.entrySet().stream()
                .filter( keyValue -> notQuoteOrDelimiter( keyValue.getKey() ) )
                .forEach( s ->
                {
                    commands.addCommand( "--" + s.getKey() );
                    commands.addCommand( s.getValue() );
                } );
    }

    private boolean notQuoteOrDelimiter( String key )
    {
        return !("quote".equalsIgnoreCase( key ) || "delimiter".equalsIgnoreCase( key ));
    }
}
