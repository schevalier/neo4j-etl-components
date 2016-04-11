package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinQueryInfo;
import org.neo4j.integration.sql.metadata.MetadataProducer;
import org.neo4j.integration.util.Loggers;

import static java.lang.String.format;

public class JoinMetadataProducer implements MetadataProducer<JoinQueryInfo, Join>
{
    private final DatabaseClient databaseClient;

    public JoinMetadataProducer( DatabaseClient databaseClient )
    {
        this.databaseClient = databaseClient;
    }

    @Override
    public Collection<Join> createMetadataFor( JoinQueryInfo source ) throws Exception
    {
        Loggers.Default.log( Level.INFO, format( "Generating metadata for join       [%s -> %s]",
                source.tableOne(), source.tableTwo() ) );
        String sql = select( source );

        Collection<Join> joins = new ArrayList<>();

        try ( QueryResults results = databaseClient.executeQuery( sql ).await() )
        {
            Map<String, List<Map<String, String>>> joinsGroupedByStartTable = results.stream()
                    .collect( Collectors.groupingBy( row -> row.get( "SOURCE_TABLE_NAME" ) ) );

            joinsGroupedByStartTable.entrySet().stream()
                    .forEach( entry -> joins.add( buildJoin( entry.getValue() ) ) );

        }

        return joins;
    }

    private Join buildJoin( List<Map<String, String>> rows )
    {
        return new JoinKeysQueryResults( rows ).createJoin();
    }

    private String select( JoinQueryInfo source )
    {
        return "SELECT DISTINCT join_table.TABLE_SCHEMA AS SOURCE_TABLE_SCHEMA," +
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
                " WHERE join_table.TABLE_SCHEMA = '" + source.table().schema() + "'" +
                " AND join_table.TABLE_NAME = '" + source.table().simpleName() + "'" +
                " AND " + source.specialisedSql() + " ;";
    }
}
