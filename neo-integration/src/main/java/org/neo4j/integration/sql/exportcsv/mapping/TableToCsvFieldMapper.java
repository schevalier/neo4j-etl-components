package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.Collection;
import java.util.function.BiPredicate;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.sql.RowAccessor;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.SimpleColumn;
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
                case CompositeKey:
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
                new SimpleColumn(
                        table.name(),
                        formatting.quote().enquote( table.name().simpleName() ),
                        table.name().simpleName(),
                        ColumnType.Literal,
                        SqlDataType.LABEL_DATA_TYPE ),
                CsvField.label() );

        return builder.build();
    }

    @Override
    public BiPredicate<RowAccessor, Collection<Column>> writeRowWithNullsStrategy()
    {
        return ( r, c ) -> true;
    }
}
