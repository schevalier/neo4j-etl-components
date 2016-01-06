package org.neo4j.ingest;

public interface FieldType
{
    void validate(boolean fieldHasName);
}
