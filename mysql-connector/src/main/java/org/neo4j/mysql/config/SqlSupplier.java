package org.neo4j.mysql.config;

import java.nio.file.Path;

import org.neo4j.ingest.config.Delimiter;

public interface SqlSupplier
{
    String sql( Path exportFile, Delimiter delimiter );
}
