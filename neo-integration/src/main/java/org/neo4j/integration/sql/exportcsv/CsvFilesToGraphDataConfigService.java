package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;

public interface CsvFilesToGraphDataConfigService
{
    GraphDataConfig createGraphDataConfig(Collection<Path> csvFiles);
}
