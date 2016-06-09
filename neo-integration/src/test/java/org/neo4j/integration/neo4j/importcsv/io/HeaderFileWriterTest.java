package org.neo4j.integration.neo4j.importcsv.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.GraphObjectType;
import org.neo4j.integration.neo4j.importcsv.config.formatting.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.neo4j.importcsv.fields.Neo4jDataType;
import org.neo4j.integration.util.ResourceRule;

import static java.util.Collections.singletonList;

import static org.junit.Assert.assertEquals;

import static org.neo4j.integration.util.TemporaryDirectory.temporaryDirectory;

public class HeaderFileWriterTest
{
    @Rule
    public final ResourceRule<Path> tempDirectory = new ResourceRule<>( temporaryDirectory() );

    @Test
    public void shouldCreateHeaderFileForFields() throws IOException
    {
        // given
        HeaderFileWriter headerFileWriter = new HeaderFileWriter( tempDirectory.get(), Formatting.DEFAULT );

        // when
        Collection<CsvField> fields = new ArrayList<>();
        fields.add( CsvField.id( "personId", new IdSpace( "person" ) ) );
        fields.add( CsvField.label() );
        fields.add( CsvField.data( "name", Neo4jDataType.String ) );
        fields.add( CsvField.array( "addresses", Neo4jDataType.String ) );

        Path file = headerFileWriter.writeHeaderFile( GraphObjectType.Node.name(), fields, "nodes" );

        // then
        List<String> expectedLines = singletonList( "personId:ID(person),:LABEL,name:string,addresses:string[]" );

        assertEquals( expectedLines, Files.readAllLines( file ) );
    }

}
