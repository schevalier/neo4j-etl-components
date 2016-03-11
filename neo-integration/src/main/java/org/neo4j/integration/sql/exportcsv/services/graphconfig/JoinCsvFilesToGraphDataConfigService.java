package org.neo4j.integration.sql.exportcsv.services.graphconfig;

import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.RelationshipConfig;
import org.neo4j.integration.sql.exportcsv.CsvFilesToGraphDataConfigService;
import org.neo4j.integration.sql.exportcsv.io.CsvFiles;

class JoinCsvFilesToGraphDataConfigService implements CsvFilesToGraphDataConfigService
{
    @Override
    public GraphDataConfig createGraphDataConfig( CsvFiles csvFiles )
    {
        return RelationshipConfig.builder().addInputFiles( csvFiles.asCollection() ).build();
    }
}
