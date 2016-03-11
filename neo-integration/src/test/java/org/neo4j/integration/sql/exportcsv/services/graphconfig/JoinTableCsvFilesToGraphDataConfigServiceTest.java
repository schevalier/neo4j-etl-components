package org.neo4j.integration.sql.exportcsv.services.graphconfig;

import java.nio.file.Paths;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.RelationshipConfig;
import org.neo4j.integration.sql.exportcsv.io.CsvFiles;

import static org.junit.Assert.assertEquals;

public class JoinTableCsvFilesToGraphDataConfigServiceTest
{
    @Test
    public void shouldCreateRelationshipConfig()
    {
        // given
        CsvFiles csvFiles = new CsvFiles( Paths.get( "header.csv" ), Paths.get( "body.csv" ) );
        GraphDataConfig expectedRelationshipConfig = RelationshipConfig.builder()
                .addInputFiles( csvFiles.asCollection() ).build();

        JoinTableCsvFilesToGraphDataConfigService service = new JoinTableCsvFilesToGraphDataConfigService();

        // when
        GraphDataConfig graphDataConfig = service.createGraphDataConfig( csvFiles );

        // then
        assertEquals( expectedRelationshipConfig, graphDataConfig );
    }
}
