package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.sql.metadata.Column;

public class ColumnToCsvFieldMapping
{
    private final Column column;
    private final CsvField field;

    public ColumnToCsvFieldMapping( Column column, CsvField field )
    {
        this.column = column;
        this.field = field;
    }

    public Column column()
    {
        return column;
    }

    public CsvField field()
    {
        return field;
    }
}
