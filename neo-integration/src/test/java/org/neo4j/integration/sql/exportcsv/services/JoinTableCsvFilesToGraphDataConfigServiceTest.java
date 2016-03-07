package org.neo4j.integration.sql.exportcsv.services;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.RelationshipConfig;

import static java.util.Arrays.asList;

import static org.junit.Assert.*;

public class JoinTableCsvFilesToGraphDataConfigServiceTest
{
    @Test
    public void shouldCreateRelationshipConfig()
    {
        // given
        Collection<Path> csvFiles = asList( Paths.get( "header.csv" ), Paths.get( "body.csv" ) );
        GraphDataConfig expectedRelationshipConfig = RelationshipConfig.builder().addInputFiles( csvFiles ).build();

        JoinTableCsvFilesToGraphDataConfigService service = new JoinTableCsvFilesToGraphDataConfigService();

        // when
        GraphDataConfig graphDataConfig = service.createGraphDataConfig( csvFiles );

        // then
        assertEquals( expectedRelationshipConfig, graphDataConfig );
    }
}
