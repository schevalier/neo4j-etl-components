package org.neo4j.integration.mysql.exportcsv.config;

import java.nio.file.Path;

import org.neo4j.integration.neo4j.importcsv.config.Delimiter;

public interface SqlSupplier
{
    String sql( Path exportFile, Delimiter delimiter );
}
