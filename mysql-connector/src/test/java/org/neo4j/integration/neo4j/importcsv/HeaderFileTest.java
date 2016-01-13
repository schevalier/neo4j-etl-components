package org.neo4j.integration.neo4j.importcsv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.DataType;
import org.neo4j.integration.neo4j.importcsv.config.CsvField;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.IdSpace;
import org.neo4j.integration.util.ResourceRule;

import static java.util.Collections.singletonList;

import static org.junit.Assert.assertEquals;

import static org.neo4j.integration.util.TemporaryDirectory.temporaryDirectory;

public class HeaderFileTest
{
    @Rule
    public final ResourceRule<Path> tempDirectory = new ResourceRule<>( temporaryDirectory() );

    @Test
    public void shouldCreateHeaderFileForFields() throws IOException
    {
        // given
        HeaderFile headerFile = new HeaderFile( tempDirectory.get(), Formatting.DEFAULT );

        // when
        Collection<CsvField> fields = new ArrayList<>();
        fields.add( CsvField.id( "personId", new IdSpace( "person" ) ) );
        fields.add( CsvField.label() );
        fields.add( CsvField.data( "name", DataType.String ) );
        fields.add( CsvField.array( "addresses", DataType.String ) );

        Path file = headerFile.createHeaderFile( fields, "nodes" );

        // then
        List<String> expectedLines = singletonList( "personId:ID(person),:LABEL,name:string,addresses:string[]" );

        assertEquals( expectedLines, Files.readAllLines( file ) );
    }

}
