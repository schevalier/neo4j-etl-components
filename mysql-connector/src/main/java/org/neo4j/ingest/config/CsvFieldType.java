package org.neo4j.ingest.config;

public interface CsvFieldType
{
    void validate(boolean fieldHasName);

    String value();
}
