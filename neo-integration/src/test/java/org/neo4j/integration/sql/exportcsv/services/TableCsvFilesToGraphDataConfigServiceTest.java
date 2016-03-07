package org.neo4j.integration.sql.exportcsv.services;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.NodeConfig;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.Table;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class TableCsvFilesToGraphDataConfigServiceTest
{
    @Test
    public void shouldCreateNodeConfig()
    {
        // given
        Table table = Table.builder().name( "test.Person" ).addColumn( mock( Column.class ) ).build();
        Collection<Path> csvFiles = asList( Paths.get( "header.csv" ), Paths.get( "body.csv" ) );
        GraphDataConfig expectedNodeConfig = NodeConfig.builder()
                .addInputFiles( csvFiles )
                .addLabel( "Person" )
                .build();

        TableCsvFilesToGraphDataConfigService service = new TableCsvFilesToGraphDataConfigService( table );

        // when
        GraphDataConfig graphDataConfig = service.createGraphDataConfig( csvFiles );

        // then
        assertEquals( expectedNodeConfig, graphDataConfig );
    }
}
