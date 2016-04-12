package org.neo4j.integration.neo4j.importcsv.config;

import java.nio.file.Path;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;

import org.neo4j.integration.process.Commands;
import org.neo4j.integration.util.ResourceRule;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import static org.neo4j.integration.util.TemporaryDirectory.temporaryDirectory;

public class ImportToolOptionsTest
{
    @Rule
    public final ResourceRule<Path> tempDirectory = new ResourceRule<>( temporaryDirectory() );

    @Test
    public void intialiseWithEmptyMapIfTheFileIsEmpty() throws Exception
    {
        ImportToolOptions nonExistentOptions = ImportToolOptions.initialiseFromFile( "non-existent" );
        assertTrue( nonExistentOptions.options().isEmpty() );

        Path fileWithErrors = tempDirectory.get().resolve( "fileWithErrors.json" );
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.writeValue( fileWithErrors.toFile(), new HashMap<>() );

        ImportToolOptions fileWithErrorsOptions = ImportToolOptions.initialiseFromFile( fileWithErrors.toString() );
        assertTrue( fileWithErrorsOptions.options().isEmpty() );

        Path optionsFile = tempDirectory.get().resolve( "options.json" );
        HashMap<Object, Object> options = new HashMap<>();
        options.put( "delimiter", "\t" );
        options.put( "quote", "`" );
        options.put( "multiline-fields", "true" );
        objectMapper.writeValue( optionsFile.toFile(), options );

        ImportToolOptions importToolOptions = ImportToolOptions.initialiseFromFile( optionsFile.toString() );
        assertThat( importToolOptions.options().size(), is(3) );
    }

    @Test
    public void shouldReturnAllOptionsExcludingQuotesAndFormatting() throws Exception
    {
        HashMap<String, String> options = new HashMap<>();
        options.put( "opt1", "val1" );
        options.put( "opt2", "val2" );
        options.put( "quote", "'" );
        ImportToolOptions importToolOptions = new ImportToolOptions( options );

        Commands.Builder.SetCommands mockBuilder = mock( Commands.Builder.SetCommands.class );

        importToolOptions.addOptionsAsCommands( mockBuilder );

        verify( mockBuilder ).addCommand( "--opt1" );
        verify( mockBuilder ).addCommand( "val1" );
        verify( mockBuilder ).addCommand( "--opt2" );
        verify( mockBuilder ).addCommand( "val2" );
        verifyNoMoreInteractions( mockBuilder );
    }

    @Test
    public void shouldDefaultToDefaultOptionOnlyIfNoQuoteIsNotProvidedInTheCommandAndFile() throws Exception
    {
        ImportToolOptions importToolOptions = new ImportToolOptions( new HashMap<>() );

        assertThat( importToolOptions.getDelimiter( ";" ).value(), is( ";" ) );
        assertThat( importToolOptions.getDelimiter( "" ), is( Formatting.DEFAULT_DELIMITER ) );
        assertThat( importToolOptions.getDelimiter( null ), is( Formatting.DEFAULT_DELIMITER ) );

        assertThat( importToolOptions.getQuoteCharacter( "'" ).argValue(), is("'") );
        assertThat( importToolOptions.getQuoteCharacter( "" ), is( Formatting.DEFAULT_QUOTE_CHAR ) );
        assertThat( importToolOptions.getQuoteCharacter( null ), is( Formatting.DEFAULT_QUOTE_CHAR ) );
    }

    @Test
    public void shouldReturnOptionsFileWhenCommandIsNotProvided() throws Exception
    {
        HashMap<String, String> options = new HashMap<>();
        options.put( "quote", "'" );
        options.put( "delimiter", ";" );
        ImportToolOptions importToolOptions = new ImportToolOptions( options );

        assertThat( importToolOptions.getDelimiter( "" ).value(), is( ";" ) );
        assertThat( importToolOptions.getQuoteCharacter( "" ).argValue(), is( "'" ) );
    }
}
