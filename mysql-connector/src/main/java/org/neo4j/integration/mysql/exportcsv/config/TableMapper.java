package org.neo4j.integration.mysql.exportcsv.config;

import org.neo4j.integration.mysql.exportcsv.metadata.Column;
import org.neo4j.integration.mysql.exportcsv.metadata.ColumnType;
import org.neo4j.integration.mysql.exportcsv.metadata.Table;
import org.neo4j.integration.neo4j.importcsv.config.CsvField;
import org.neo4j.integration.neo4j.importcsv.config.IdSpace;
import org.neo4j.integration.neo4j.importcsv.config.QuoteChar;

public class TableMapper implements Mapper<Table>
{
    @Override
    public ColumnToCsvFieldMappings createExportCsvConfigFor( Table table, QuoteChar quote )
    {
        ColumnToCsvFieldMappings mappings = new ColumnToCsvFieldMappings();

        for ( Column column : table.columns() )
        {
            switch ( column.type() )
            {
                case PrimaryKey:
                    mappings.add( column, CsvField.id( new IdSpace( table.name().fullName() ) ) );
                    break;
                case Data:
                    mappings.add( column, CsvField.data( column.simpleName() ) );
                    break;
                default:
                    // Do nothing
                    break;
            }
        }

        mappings.add(
                Column.builder()
                        .table( table.name() )
                        .name( quote.enquote( table.name().simpleName() ) )
                        .type( ColumnType.Literal )
                        .build(),
                CsvField.label() );

        return mappings;
    }
}
