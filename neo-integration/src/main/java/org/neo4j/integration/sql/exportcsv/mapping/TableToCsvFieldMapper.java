package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.QuoteChar;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnRole;
import org.neo4j.integration.sql.metadata.ColumnValueSelectionStrategy;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.Table;

class TableToCsvFieldMapper implements DatabaseObjectToCsvFieldMapper<Table>
{
    private final Formatting formatting;

    TableToCsvFieldMapper( Formatting formatting )
    {
        this.formatting = formatting;
    }

    @Override
    public ColumnToCsvFieldMappings createMappings( Table table )
    {
        ColumnToCsvFieldMappings.Builder builder = ColumnToCsvFieldMappings.builder();

        for ( Column column : table.columns() )
        {
            if ( column.role() == ColumnRole.PrimaryKey )
            {
                CsvField id = CsvField.id( new IdSpace( table.name().fullName() ) );
                builder.add( new ColumnToCsvFieldMapping( column, id ) );
                column.addData( builder );
            }
            else if ( column.role() == ColumnRole.Data )
            {
                column.addData( builder );
            }
        }

        SimpleColumn label = new SimpleColumn(
                table.name(),
                QuoteChar.DOUBLE_QUOTES.enquote( formatting.labelFormatter().format( table.name().simpleName() ) ),
                "_NODE_LABEL_",
                ColumnRole.Literal,
                SqlDataType.LABEL_DATA_TYPE, ColumnValueSelectionStrategy.SelectColumnValue );

        builder.add( new ColumnToCsvFieldMapping( label, CsvField.label() ) );

        return builder.build();
    }
}
