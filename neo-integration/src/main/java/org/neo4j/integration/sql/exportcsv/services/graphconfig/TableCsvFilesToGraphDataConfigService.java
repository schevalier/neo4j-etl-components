package org.neo4j.integration.sql.exportcsv.services.graphconfig;

import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.NodeConfig;
import org.neo4j.integration.sql.exportcsv.CsvFilesToGraphDataConfigService;
import org.neo4j.integration.sql.exportcsv.io.CsvFiles;

class TableCsvFilesToGraphDataConfigService implements CsvFilesToGraphDataConfigService
{
    @Override
    public GraphDataConfig createGraphDataConfig( CsvFiles csvFiles )
    {
        return NodeConfig.builder()
                .addInputFiles( csvFiles.asCollection() )
                .build();
    }
}
