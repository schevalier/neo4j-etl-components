package org.neo4j.integration.sql.exportcsv.config;

import java.nio.file.Path;

import org.neo4j.integration.sql.metadata.ConnectionConfig;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;

public interface ExportProperties
{
    Path destination();

    ConnectionConfig connectionConfig();

    Formatting formatting();
}
