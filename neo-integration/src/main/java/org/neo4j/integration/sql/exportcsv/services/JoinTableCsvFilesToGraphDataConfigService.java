package org.neo4j.integration.sql.exportcsv.services;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.RelationshipConfig;
import org.neo4j.integration.sql.exportcsv.CsvFilesToGraphDataConfigService;

class JoinTableCsvFilesToGraphDataConfigService implements CsvFilesToGraphDataConfigService
{
    @Override
    public GraphDataConfig createGraphDataConfig( Collection<Path> csvFiles )
    {
        return importConfig -> importConfig.addRelationshipConfig( RelationshipConfig.builder()
                .addInputFiles( csvFiles )
                .build() );
    }
}
