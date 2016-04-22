package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.sql.metadata.DatabaseObject;

public interface DatabaseObjectToCsvFieldMapper<T extends DatabaseObject>
{
    ColumnToCsvFieldMappings createMappings( T source );
}
