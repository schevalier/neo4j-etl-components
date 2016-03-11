package org.neo4j.integration.sql.exportcsv.io;

import java.nio.file.Paths;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.GraphObjectType;

import static java.util.Arrays.asList;

import static junit.framework.TestCase.assertEquals;

public class ManifestTest
{
    @Test
    public void shouldAddCsvFiles()
    {
        // given
        CsvFiles nodeCsvFiles1 = new CsvFiles( Paths.get( "header.csv" ), Paths.get( "body.csv" ) );
        CsvFiles nodeCsvFiles2 = new CsvFiles( Paths.get( "header.csv" ), Paths.get( "body.csv" ) );
        CsvFiles relationshipCsvFiles1 = new CsvFiles( Paths.get( "header.csv" ), Paths.get( "body.csv" ) );
        CsvFiles relationshipCsvFiles2 = new CsvFiles( Paths.get( "header.csv" ), Paths.get( "body.csv" ) );
        Manifest manifest = new Manifest();

        // when
        manifest.add( new ManifestEntry( GraphObjectType.Node, nodeCsvFiles1 ) )
                .add( new ManifestEntry( GraphObjectType.Node, nodeCsvFiles2 ) )
                .add( new ManifestEntry( GraphObjectType.Relationship, relationshipCsvFiles1 ) )
                .add( new ManifestEntry( GraphObjectType.Relationship, relationshipCsvFiles2 ) );

        // then
        assertEquals( manifest.csvFilesForNodes(), asList( nodeCsvFiles1, nodeCsvFiles2 ) );
        assertEquals( manifest.csvFilesForRelationships(), asList( relationshipCsvFiles1, relationshipCsvFiles2 ) );
    }
}
