package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.EnumSet;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.QuoteChar;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnRole;
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
            if ( column.roles().contains( ColumnRole.PrimaryKey ) )
            {
                CsvField id = CsvField.id( new IdSpace( table.name().fullName() ) );
                builder.add( new ColumnToCsvFieldMapping( column, id ) );
                column.addData( builder );
            }
            else if ( column.roles().contains( ColumnRole.CompositeKey ) )
            {
                CsvField id1 = CsvField.id( new IdSpace( table.name().fullName() ) );
                builder.add( new ColumnToCsvFieldMapping( column, id1 ) );
                column.addData( builder );
            }
            else if ( column.roles().contains( ColumnRole.Data ) )
            {
                column.addData( builder );
            }
        }

        SimpleColumn label = new SimpleColumn(
                table.name(),
                QuoteChar.DOUBLE_QUOTES.enquote( formatting.labelFormatter().format( table.name().simpleName() ) ),
                table.name().simpleName(),
                EnumSet.of(ColumnRole.Literal),
                SqlDataType.LABEL_DATA_TYPE );

        builder.add( new ColumnToCsvFieldMapping( label, CsvField.label() ) );

        return builder.build();
    }
}
