package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.Collection;

import org.neo4j.integration.sql.RowAccessor;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.DatabaseObject;
import org.neo4j.integration.util.BiPredicate;

public interface DatabaseObjectToCsvFieldMapper<T extends DatabaseObject>
{
    ColumnToCsvFieldMappings createMappings( T source );

    BiPredicate<RowAccessor, Collection<Column>> writeRowWithNullsStrategy();
}
