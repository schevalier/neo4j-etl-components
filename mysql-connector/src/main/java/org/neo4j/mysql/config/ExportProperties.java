package org.neo4j.mysql.config;

import java.nio.file.Path;

import org.neo4j.ingest.config.Formatting;

public interface ExportProperties
{
    Path destination();

    MySqlConnectionConfig connectionConfig();

    Formatting formatting();
}
