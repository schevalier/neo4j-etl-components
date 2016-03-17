package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinKey;
import org.neo4j.integration.sql.metadata.MetadataProducer;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.sql.metadata.TableNamePair;

public class JoinMetadataProducer implements MetadataProducer<TableNamePair, Join>
{
    private final DatabaseClient databaseClient;

    public JoinMetadataProducer( DatabaseClient databaseClient )
    {
        this.databaseClient = databaseClient;
    }

    @Override
    public Collection<Join> createMetadataFor( TableNamePair source ) throws Exception
    {
        String sql = select( source.startTable(), source.endTable() );

        Collection<Join> joins = new ArrayList<>();

        try ( QueryResults results = databaseClient.executeQuery( sql ).await() )
        {
            Map<String, List<Map<String, String>>> joinsGroupedByStartTable = results.streamOfResults()
                    .collect( Collectors.groupingBy( row -> row.get( "SOURCE_TABLE_NAME" ) ) );
            for ( Map.Entry<String, List<Map<String, String>>> entry : joinsGroupedByStartTable.entrySet() )
            {
                Map<String, String> primaryKeyMetadata = entry.getValue().stream()
                        .filter( row ->
                                row.get( "SOURCE_COLUMN_TYPE" ).equalsIgnoreCase( ColumnType.PrimaryKey.name() ) )
                        .findFirst().get();
                Map<String, String> foreignKeyMetadata = entry.getValue().stream()
                        .filter( row ->
                                row.get( "SOURCE_COLUMN_TYPE" ).equalsIgnoreCase( ColumnType.ForeignKey.name() ) )
                        .findFirst().get();

                JoinKey left = createJoin( primaryKeyMetadata );
                JoinKey right = createJoin( foreignKeyMetadata );
                joins.add( new Join( left, right, left.source().table() ) );
            }
        }
        return joins;
    }

    private JoinKey createJoin( Map<String, String> results )
    {
        TableName sourceTable = new TableName(
                results.get( "SOURCE_TABLE_SCHEMA" ),
                results.get( "SOURCE_TABLE_NAME" ) );
        TableName targetTable = new TableName(
                results.get( "TARGET_TABLE_SCHEMA" ),
                results.get( "TARGET_TABLE_NAME" ) );
        String sourceColumn = results.get( "SOURCE_COLUMN_NAME" );
        String targetColumn = results.get( "TARGET_COLUMN_NAME" );
        ColumnType sourceColumnType = ColumnType.valueOf( results.get( "SOURCE_COLUMN_TYPE" ) );
        ColumnType targetColumnType = ColumnType.valueOf( results.get( "TARGET_COLUMN_TYPE" ) );

        return new JoinKey(
                new SimpleColumn(
                        sourceTable,
                        sourceTable.fullyQualifiedColumnName( sourceColumn ),
                        sourceColumn,
                        sourceColumnType,
                        SqlDataType.KEY_DATA_TYPE ),
                new SimpleColumn(
                        targetTable,
                        targetTable.fullyQualifiedColumnName( targetColumn ),
                        targetColumn,
                        targetColumnType,
                        SqlDataType.KEY_DATA_TYPE ) );
    }

    private String select( TableName t1, TableName t2 )
    {
        return "SELECT join_table.TABLE_SCHEMA AS SOURCE_TABLE_SCHEMA," +
                "        CASE ( SELECT COUNT(referenced_table.REFERENCED_TABLE_NAME) " +
                "               FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE referenced_table " +
                "               WHERE source_column.TABLE_SCHEMA = referenced_table.TABLE_SCHEMA " +
                "               AND source_column.TABLE_NAME = referenced_table.TABLE_NAME " +
                "               AND source_column.COLUMN_NAME = referenced_table.COLUMN_NAME )" +
                "    WHEN 0 THEN " +
                "      CASE source_column.COLUMN_KEY" +
                "        WHEN 'PRI' THEN 'PrimaryKey'" +
                "        ELSE 'Data'" +
                "      END" +
                "      ELSE 'ForeignKey'" +
                "    END AS SOURCE_COLUMN_TYPE," +
                "    join_table.TABLE_NAME AS SOURCE_TABLE_NAME," +
                "    join_table.COLUMN_NAME AS SOURCE_COLUMN_NAME," +
                "        CASE ( SELECT COUNT(referenced_table.REFERENCED_TABLE_NAME) " +
                "               FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE referenced_table " +
                "               WHERE target_column.TABLE_SCHEMA = referenced_table.TABLE_SCHEMA " +
                "               AND target_column.TABLE_NAME = referenced_table.TABLE_NAME " +
                "               AND target_column.COLUMN_NAME = referenced_table.COLUMN_NAME )" +
                "    WHEN 0 THEN" +
                "      CASE target_column.COLUMN_KEY" +
                "        WHEN 'PRI' THEN 'PrimaryKey'" +
                "        ELSE 'Data'" +
                "      END" +
                "      ELSE 'ForeignKey'" +
                "    END AS TARGET_COLUMN_TYPE," +
                "    IFNULL(join_table.REFERENCED_TABLE_SCHEMA,join_table.TABLE_SCHEMA) AS TARGET_TABLE_SCHEMA," +
                "    IFNULL(join_table.REFERENCED_TABLE_NAME,join_table.TABLE_NAME) AS TARGET_TABLE_NAME," +
                "    IFNULL(join_table.REFERENCED_COLUMN_NAME,join_table.COLUMN_NAME) AS TARGET_COLUMN_NAME" +
                " FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE join_table " +
                " LEFT OUTER JOIN INFORMATION_SCHEMA.COLUMNS AS source_column ON " +
                "  (join_table.TABLE_SCHEMA = source_column.TABLE_SCHEMA " +
                "   AND join_table.TABLE_NAME = source_column.TABLE_NAME " +
                "   AND join_table.COLUMN_NAME = source_column.COLUMN_NAME) " +
                " LEFT OUTER JOIN INFORMATION_SCHEMA.COLUMNS AS target_column ON" +
                "  (IFNULL(join_table.REFERENCED_TABLE_SCHEMA,join_table.TABLE_SCHEMA) = target_column.TABLE_SCHEMA " +
                "   AND IFNULL(join_table.REFERENCED_TABLE_NAME,join_table.TABLE_NAME) = target_column.TABLE_NAME " +
                "   AND IFNULL(join_table.REFERENCED_COLUMN_NAME,join_table.COLUMN_NAME) = target_column.COLUMN_NAME)" +
                " WHERE join_table.TABLE_SCHEMA = '" + t1.schema() + "'" +
                " AND join_table.TABLE_NAME = '" + t1.simpleName() + "'" +
                " AND " +
                " (" +
                "  (source_column.COLUMN_KEY = 'PRI' AND join_table.REFERENCED_TABLE_NAME IS NULL) OR " +
                "    (join_table.REFERENCED_TABLE_NAME IN ('" + t2.simpleName() + "'))" +
                " );";
    }
}
