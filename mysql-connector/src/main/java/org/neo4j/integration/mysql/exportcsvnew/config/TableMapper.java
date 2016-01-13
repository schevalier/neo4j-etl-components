package org.neo4j.integration.mysql.exportcsvnew.config;

import org.neo4j.integration.mysql.exportcsvnew.metadata.Column;
import org.neo4j.integration.mysql.exportcsvnew.metadata.ColumnType;
import org.neo4j.integration.mysql.exportcsvnew.metadata.Table;
import org.neo4j.integration.neo4j.importcsv.config.CsvField;
import org.neo4j.integration.neo4j.importcsv.config.IdSpace;
import org.neo4j.integration.neo4j.importcsv.config.QuoteChar;

public class TableMapper
{
    private final QuoteChar quote;

    public TableMapper( QuoteChar quote )
    {
        this.quote = quote;
    }

    public ColumnToCsvFieldMappings createExportCsvConfigFor( Table table )
    {
        ColumnToCsvFieldMappings config = new ColumnToCsvFieldMappings();

        for ( Column column : table.columns() )
        {
            switch ( column.type() )
            {
                case PrimaryKey:
                    config.addMapping( column, CsvField.id( new IdSpace( table.name().fullName() ) ) );
                    break;
                case Data:
                    config.addMapping( column, CsvField.data( column.name() ) );
                    break;
                default:
                    // Do nothing
                    break;
            }
        }

        config.addMapping(
                Column.builder()
                        .table( table.name() )
                        .name( quote.enquote( table.name().simpleName() ) )
                        .type( ColumnType.Literal )
                        .build(),
                CsvField.label() );

        return config;
    }
}
