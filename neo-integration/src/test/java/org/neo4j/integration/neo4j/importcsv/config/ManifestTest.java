package org.neo4j.integration.neo4j.importcsv.config;

import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ManifestTest
{
    @Test
    public void shouldAddNodeAndRelationshipConfigToImportConfig()
    {
        // given
        CsvFiles nodeCsvFiles1 = new CsvFiles( Paths.get( "header1.csv" ), Paths.get( "body1.csv" ) );
        CsvFiles nodeCsvFiles2 = new CsvFiles( Paths.get( "header2.csv" ), Paths.get( "body2.csv" ) );
        CsvFiles relationshipCsvFiles1 = new CsvFiles( Paths.get( "header3.csv" ), Paths.get( "body3.csv" ) );
        CsvFiles relationshipCsvFiles2 = new CsvFiles( Paths.get( "header4.csv" ), Paths.get( "body4.csv" ) );

        Manifest manifest = new Manifest();
        manifest.add( new ManifestEntry( GraphObjectType.Node, nodeCsvFiles1 ) )
                .add( new ManifestEntry( GraphObjectType.Node, nodeCsvFiles2 ) )
                .add( new ManifestEntry( GraphObjectType.Relationship, relationshipCsvFiles1 ) )
                .add( new ManifestEntry( GraphObjectType.Relationship, relationshipCsvFiles2 ) );

        ImportConfig.Builder builder = mock( ImportConfig.Builder.class );

        // when
        manifest.addNodesAndRelationshipsToBuilder( builder );

        // then
        verify(builder).addNodeConfig( NodeConfig.builder().addInputFiles( nodeCsvFiles1.asCollection() ).build() );
        verify(builder).addNodeConfig( NodeConfig.builder().addInputFiles( nodeCsvFiles2.asCollection() ).build() );
        verify(builder).addRelationshipConfig(
                RelationshipConfig.builder().addInputFiles( relationshipCsvFiles1.asCollection() ).build() );
        verify(builder).addRelationshipConfig(
                RelationshipConfig.builder().addInputFiles( relationshipCsvFiles2.asCollection() ).build() );
    }
}
