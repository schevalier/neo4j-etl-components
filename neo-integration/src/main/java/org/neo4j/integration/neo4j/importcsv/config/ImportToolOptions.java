package org.neo4j.integration.neo4j.importcsv.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import org.neo4j.integration.process.Commands;
import org.neo4j.integration.util.Loggers;

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

    public static ImportToolOptions initialiseFromFile( String optionsFile )
    {
        ObjectMapper objectMapper = new ObjectMapper();
        if ( StringUtils.isNotEmpty( optionsFile ) && Files.exists( Paths.get( optionsFile ) ) )
        {
            File path = Paths.get( optionsFile ).toFile();
            try
            {
                return new ImportToolOptions(
                        objectMapper.readValue( path, new HashMap<String, String>().getClass() ) );
            }
            catch ( IOException e )
            {
                Loggers.Default.log( Level.WARNING, "Skipping reading options from file due to error.", e );
                return new ImportToolOptions( Collections.emptyMap() );
            }
        }
        else
        {
            Loggers.Default.log( Level.INFO, "Skipping reading import options from file. File doesn't exist" );
            return new ImportToolOptions( Collections.emptyMap() );
        }
    }

    public Delimiter getDelimiter( String delimiter )
    {
        Map<String, String> importToolOptions = this.importToolOptions;
        if ( StringUtils.isNotEmpty( delimiter ) )
        {
            return new Delimiter( delimiter );
        }
        else if ( !importToolOptions.isEmpty() )
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
        Map<String, String> importToolOptions = this.importToolOptions;
        if ( StringUtils.isNotEmpty( quote ) )
        {
            return new QuoteChar( quote, quote );
        }
        else if ( !importToolOptions.isEmpty() )
        {
            return new QuoteChar(
                    importToolOptions.get( "quote" ), importToolOptions.get( "quote" ) );
        }
        else
        {
            return Formatting.DEFAULT_QUOTE_CHAR;
        }
    }

    public void addOptionsAsCommands( Commands.Builder.SetCommands commands )
    {
        importToolOptions.entrySet().stream()
                .filter(
                        keyValue -> notQuoteOrDelimiter( keyValue.getKey() ) )
                .forEach( s -> {
                    commands.addCommand( "--" + s.getKey() );
                    commands.addCommand( s.getValue() );
                } );
    }

    private boolean notQuoteOrDelimiter( String key )
    {
        return !("quote".equalsIgnoreCase( key ) ||
                "delimiter".equalsIgnoreCase( key ));
    }
}
