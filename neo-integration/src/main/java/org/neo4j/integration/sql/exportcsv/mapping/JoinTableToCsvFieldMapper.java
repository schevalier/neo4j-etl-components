package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.Collection;
import java.util.function.BiPredicate;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.sql.RowAccessor;
import org.neo4j.integration.sql.exportcsv.io.WriteRowWithNullsStrategy;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.TableName;

public class JoinTableToCsvFieldMapper implements DatabaseObjectToCsvFieldMapper<JoinTable>
{
    private final Formatting formatting;

    public JoinTableToCsvFieldMapper( Formatting formatting )
    {
        this.formatting = formatting;
    }

    @Override
    public BiPredicate<RowAccessor, Collection<Column>> writeRowWithNullsStrategy()
    {
        return new WriteRowWithNullsStrategy();
    }

    @Override
    public ColumnToCsvFieldMappings createMappings( JoinTable joinTable )
    {
        ColumnToCsvFieldMappings.Builder builder = ColumnToCsvFieldMappings.builder();

        builder.add( joinTable.join().keyOneSourceColumn(),
                CsvField.startId( new IdSpace( joinTable.join().keyOneTargetColumn().table().fullName() ) ) );
        builder.add( joinTable.join().keyTwoSourceColumn(),
                CsvField.endId( new IdSpace( joinTable.join().keyTwoTargetColumn().table().fullName() ) ) );

        TableName table = joinTable.joinTableName();

        String relationshipType = table.simpleName().toUpperCase();

        builder.add(
                new SimpleColumn( table,
                        formatting.quote().enquote( relationshipType ),
                        relationshipType,
                        ColumnType.Literal,
                        SqlDataType.RELATIONSHIP_TYPE_DATA_TYPE ),
                CsvField.relationshipType() );

        addProperties( joinTable, builder );

        return builder.build();
    }

    private void addProperties( JoinTable joinTable, ColumnToCsvFieldMappings.Builder builder )
    {
        for ( Column column : joinTable.columns() )
        {
            switch ( column.type() )
            {
                case PrimaryKey:
                    builder.add( column, CsvField.id( new IdSpace( joinTable.joinTableName().fullName() ) ) );
                    break;
                case Data:
                    builder.add( column, CsvField.data( column.alias(), column.dataType().toNeo4jDataType() ) );
                    break;
                default:
                    // Do nothing
                    break;
            }
        }
    }
}
