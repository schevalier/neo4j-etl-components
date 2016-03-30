package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.Collection;
import java.util.function.BiPredicate;

import org.neo4j.integration.sql.RowAccessor;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.DatabaseObject;

public interface DatabaseObjectToCsvFieldMapper<T extends DatabaseObject>
{
    ColumnToCsvFieldMappings createMappings( T source );
}
