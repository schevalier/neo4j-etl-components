package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.sql.metadata.DatabaseObject;

public interface Mapper<T extends DatabaseObject>
{
    ColumnToCsvFieldMappings createMappings( T source );
}
