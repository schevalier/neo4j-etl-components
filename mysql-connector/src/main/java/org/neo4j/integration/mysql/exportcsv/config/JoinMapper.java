package org.neo4j.integration.mysql.exportcsv.config;

import org.neo4j.integration.mysql.exportcsv.metadata.Column;
import org.neo4j.integration.mysql.exportcsv.metadata.ColumnType;
import org.neo4j.integration.mysql.exportcsv.metadata.Join;
import org.neo4j.integration.neo4j.importcsv.config.CsvField;
import org.neo4j.integration.neo4j.importcsv.config.IdSpace;
import org.neo4j.integration.neo4j.importcsv.config.QuoteChar;

public class JoinMapper implements Mapper<Join>
{
    @Override
    public ColumnToCsvFieldMappings createExportCsvConfigFor( Join join, QuoteChar quote )
    {
        ColumnToCsvFieldMappings mappings = new ColumnToCsvFieldMappings();

        mappings.add( join.primaryKey(), CsvField.startId( new IdSpace( join.primaryKey().table().fullName() ) ) );
        mappings.add( join.foreignKey(), CsvField.endId( new IdSpace( join.childTable().fullName() ) ) );
        mappings.add(
                Column.builder()
                        .table( join.primaryKey().table() )
                        .name( quote.enquote( join.childTable().simpleName() ) )
                        .type( ColumnType.Literal ).build(),
                CsvField.relationshipType() );

        return mappings;
    }
}
