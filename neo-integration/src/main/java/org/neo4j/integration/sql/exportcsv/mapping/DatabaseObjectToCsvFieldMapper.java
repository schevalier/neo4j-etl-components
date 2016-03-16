package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.sql.metadata.DatabaseObject;
import org.neo4j.integration.util.BiPredicate;

public interface DatabaseObjectToCsvFieldMapper<T extends DatabaseObject>
{
    ColumnToCsvFieldMappings createMappings( T source );

    BiPredicate writeRowWithNullsStrategy();
}
