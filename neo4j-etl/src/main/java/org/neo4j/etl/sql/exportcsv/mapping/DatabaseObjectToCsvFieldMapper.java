package org.neo4j.etl.sql.exportcsv.mapping;

import org.neo4j.etl.sql.metadata.DatabaseObject;

public interface DatabaseObjectToCsvFieldMapper<T extends DatabaseObject>
{
    ColumnToCsvFieldMappings createMappings( T source );
}
