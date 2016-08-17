package org.neo4j.etl.environment;

import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.neo4j.etl.util.ResourceRule;
import org.neo4j.etl.util.TemporaryDirectory;

import static org.hamcrest.CoreMatchers.startsWith;

public class ImportToolDirectorySupplierTest
{
    @Rule
    public final ResourceRule<Path> tempDirectory = new ResourceRule<>( TemporaryDirectory.temporaryDirectory() );

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldThrowExceptionIfImportToolCannotBeFound() throws Exception
    {
        // given
        thrown.expect( IllegalArgumentException.class );
        ImportToolDirectorySupplier importToolDirectorySupplier = new ImportToolDirectorySupplier( tempDirectory.get
                () );

        //when
        importToolDirectorySupplier.supply();

        // then
        thrown.expectMessage( startsWith( "Unable to find import tool" ) );
    }
}
