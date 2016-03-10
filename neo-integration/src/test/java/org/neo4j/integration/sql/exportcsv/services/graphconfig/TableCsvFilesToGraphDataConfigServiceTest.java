package org.neo4j.integration.sql.exportcsv.services.graphconfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.NodeConfig;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;

public class TableCsvFilesToGraphDataConfigServiceTest
{
    @Test
    public void shouldCreateNodeConfig()
    {
        // given
        Collection<Path> csvFiles = asList( Paths.get( "header.csv" ), Paths.get( "body.csv" ) );
        GraphDataConfig expectedNodeConfig = NodeConfig.builder()
                .addInputFiles( csvFiles )
                .build();

        TableCsvFilesToGraphDataConfigService service = new TableCsvFilesToGraphDataConfigService();

        // when
        GraphDataConfig graphDataConfig = service.createGraphDataConfig( csvFiles );

        // then
        assertEquals( expectedNodeConfig, graphDataConfig );
    }
}
