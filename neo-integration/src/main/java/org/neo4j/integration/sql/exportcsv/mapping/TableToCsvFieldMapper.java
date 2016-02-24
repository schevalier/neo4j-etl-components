package org.neo4j.integration.sql.exportcsv.mapping;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.Table;

public class TableToCsvFieldMapper implements DatabaseObjectToCsvFieldMapper<Table>
{
    private final Formatting formatting;

    public TableToCsvFieldMapper( Formatting formatting )
    {
        this.formatting = formatting;
    }

    @Override
    public ColumnToCsvFieldMappings createMappings( Table table )
    {
        ColumnToCsvFieldMappings.Builder builder = ColumnToCsvFieldMappings.builder();

        for ( Column column : table.columns() )
        {
            switch ( column.type() )
            {
                case PrimaryKey:
                    builder.add( column, CsvField.id( new IdSpace( table.name().fullName() ) ) );
                    break;
                case Data:
                    builder.add( column, CsvField.data( column.alias(), column.dataType().toNeo4jDataType() ) );
                    break;
                default:
                    // Do nothing
                    break;
            }
        }

        builder.add(
                Column.builder()
                        .table( table.name() )
                        .name( formatting.quote().enquote( table.name().simpleName() ) )
                        .alias( table.name().simpleName() )
                        .columnType( ColumnType.Literal )
                        .dataType( SqlDataType.LABEL_DATA_TYPE )
                        .build(),
                CsvField.label() );

        return builder.build();
    }
}
