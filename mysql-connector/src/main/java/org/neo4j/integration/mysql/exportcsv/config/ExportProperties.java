package org.neo4j.integration.mysql.exportcsv.config;

import java.nio.file.Path;

import org.neo4j.integration.mysql.exportcsv.metadata.ConnectionConfig;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;

public interface ExportProperties
{
    Path destination();

    ConnectionConfig connectionConfig();

    Formatting formatting();
}
