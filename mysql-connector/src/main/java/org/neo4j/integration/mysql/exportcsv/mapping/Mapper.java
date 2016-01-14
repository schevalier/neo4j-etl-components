package org.neo4j.integration.mysql.exportcsv.mapping;

import org.neo4j.integration.mysql.metadata.DatabaseObject;

public interface Mapper<T extends DatabaseObject>
{
    ColumnToCsvFieldMappings createMappings( T source );
}
