package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.CompositeColumn;
import org.neo4j.integration.sql.metadata.JoinKey;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.util.Preconditions;

class JoinKeyQueryResults
{
    private static final Function<String, String> SOURCE = s -> "SOURCE" + s;
    private static final Function<String, String> TARGET = s -> "TARGET" + s;

    private final List<Map<String, String>> rows;

    JoinKeyQueryResults( List<Map<String, String>> rows )
    {
        this.rows = Preconditions.requireNonEmptyList( rows, "Rows" );
    }

    JoinKey createJoinKey()
    {
        if ( isCompositeKey() )
        {
            return new JoinKey( compositeColumn( rows, SOURCE ), compositeColumn( rows, TARGET ) );
        }
        else
        {
            Map<String, String> row = rows.get( 0 );

            return new JoinKey( simpleColumn( row, SOURCE ), simpleColumn( row, TARGET ) );
        }
    }

    private boolean isCompositeKey()
    {
        return rows.size() > 1;
    }

    private Column simpleColumn( Map<String, String> row, Function<String, String> prefix )
    {
        TableName targetTable = new TableName(
                row.get( prefix.apply( "_TABLE_SCHEMA" ) ), row.get( prefix.apply( "_TABLE_NAME" ) ) );
        String targetColumn = row.get( prefix.apply( "_COLUMN_NAME" ) );
        ColumnType targetColumnType = ColumnType.valueOf( row.get( prefix.apply( "_COLUMN_TYPE" ) ) );

        return new SimpleColumn(
                targetTable,
                targetTable.fullyQualifiedColumnName( targetColumn ),
                targetColumn,
                targetColumnType,
                SqlDataType.KEY_DATA_TYPE.toNeo4jDataType() );
    }

    private Column compositeColumn( List<Map<String, String>> rows, Function<String, String> prefix )
    {
        TableName table = new TableName(
                rows.get( 0 ).get( prefix.apply( "_TABLE_SCHEMA" ) ),
                rows.get( 0 ).get( prefix.apply( "_TABLE_NAME" ) ) );

        Collection<Column> columns = new ArrayList<>();

        for ( Map<String, String> row : rows )
        {
            columns.add( simpleColumn( row, prefix ) );
        }

        return new CompositeColumn( table, columns );
    }
}
