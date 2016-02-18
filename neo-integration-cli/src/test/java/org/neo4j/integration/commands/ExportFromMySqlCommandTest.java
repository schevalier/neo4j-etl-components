package org.neo4j.integration.commands;

import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.integration.util.ResourceRule;
import org.neo4j.integration.util.TemporaryDirectory;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ExportFromMySqlCommandTest
{
    @Rule
    public final ResourceRule<Path> tempDirectory = new ResourceRule<>( TemporaryDirectory.temporaryDirectory() );

    @Test
    public void shouldThrowExceptionIfImportToolCannotBeFound()
    {
        // given
        ExportFromMySqlCommand command = new ExportFromMySqlCommand(
                "localhost",
                3306,
                "neo",
                "neo",
                "javabase",
                tempDirectory.get(),
                tempDirectory.get(),
                tempDirectory.get(),
                "parent",
                "child" );

        try
        {
            // when
            command.execute();
            fail( "Expected IllegalArgumentException" );
        }
        catch ( Exception e )
        {
            // then
            assertThat( e.getMessage(), startsWith( "Unable to find import tool" ) );
        }

    }
}
