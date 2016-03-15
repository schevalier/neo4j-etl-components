package org.neo4j.integration.sql.exportcsv.mapping;

import java.util.function.BiPredicate;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.fields.CsvField;
import org.neo4j.integration.neo4j.importcsv.fields.IdSpace;
import org.neo4j.integration.sql.exportcsv.io.WriteRowWithNullsStrategy;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.JoinTable;
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
    public BiPredicate writeRowWithNullsStrategy()
    {
        return new WriteRowWithNullsStrategy();
    }

    @Override
    public ColumnToCsvFieldMappings createMappings( JoinTable joinTable )
    {
        ColumnToCsvFieldMappings.Builder builder = ColumnToCsvFieldMappings.builder();

        builder.add( joinTable.startForeignKey(),
                CsvField.startId( new IdSpace( joinTable.startPrimaryKey().table().fullName() ) ) );
        builder.add( joinTable.endForeignKey(),
                CsvField.endId( new IdSpace( joinTable.endPrimaryKey().table().fullName() ) ) );

        TableName table = joinTable.joinTableName();

        String relationshipType = table.simpleName().toUpperCase();

        builder.add(
                new Column( table,
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
