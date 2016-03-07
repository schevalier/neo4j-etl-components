package org.neo4j.integration.sql.metadata;

public interface DatabaseObject
{
    String descriptor();

    <T> T createService( MetadataServiceProvider<T> metadataServiceProvider );
}
