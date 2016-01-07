package org.neo4j.ingest.config;

public interface FieldType
{
    void validate(boolean fieldHasName);
}
