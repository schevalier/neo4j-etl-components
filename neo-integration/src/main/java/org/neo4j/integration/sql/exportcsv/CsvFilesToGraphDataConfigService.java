package org.neo4j.integration.sql.exportcsv;

import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.sql.exportcsv.io.CsvFiles;

public interface CsvFilesToGraphDataConfigService
{
    GraphDataConfig createGraphDataConfig( CsvFiles csvFiles );
}
