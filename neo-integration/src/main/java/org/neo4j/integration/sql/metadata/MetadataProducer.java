package org.neo4j.integration.sql.metadata;

import java.util.Collection;

public interface MetadataProducer<SOURCE, TARGET extends DatabaseObject>
{
    Collection<TARGET> createMetadataFor( SOURCE source ) throws Exception;
}
