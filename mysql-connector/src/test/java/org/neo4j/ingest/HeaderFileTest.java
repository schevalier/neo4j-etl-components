package org.neo4j.ingest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.ingest.config.DataType;
import org.neo4j.ingest.config.Field;
import org.neo4j.ingest.config.Formatting;
import org.neo4j.ingest.config.IdSpace;
import org.neo4j.utils.ResourceRule;

import static java.util.Collections.singletonList;

import static org.junit.Assert.assertEquals;

import static org.neo4j.utils.TemporaryDirectory.temporaryDirectory;

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
        Collection<Field> fields = new ArrayList<>();
        fields.add( Field.id( "personId", new IdSpace( "person" ) ) );
        fields.add( Field.label() );
        fields.add( Field.data( "name", DataType.String ) );
        fields.add( Field.array( "addresses", DataType.String ) );

        Path file = headerFile.create( fields, "nodes" );

        // then
        List<String> expectedLines = singletonList( "personId:ID(person),:LABEL,name:string,addresses:string[]" );

        assertEquals( expectedLines, Files.readAllLines( file ) );
    }

}
