package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.CompositeKeyColumn;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinKey;
import org.neo4j.integration.sql.metadata.JoinQueryInfo;
import org.neo4j.integration.sql.metadata.MetadataProducer;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.TableName;

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
        String sql = select( source );

        Collection<Join> joins = new ArrayList<>();

        try ( QueryResults results = databaseClient.executeQuery( sql ).await() )
        {
            Map<String, List<Map<String, String>>> joinsGroupedByStartTable = results.stream()
                    .sorted( sortOrderBasedOnSourceColTypeAndName() )
                    .collect( Collectors.groupingBy( row -> row.get( "SOURCE_TABLE_NAME" ) ) );

            joinsGroupedByStartTable.entrySet().stream()
                    .forEach( entry -> joins.add( buildJoin( source, entry.getValue() ) ) );

        }
        return joins;
    }

    private Comparator<Map<String, String>> sortOrderBasedOnSourceColTypeAndName()
    {
        return ( o1, o2 ) ->
        {
            String columnType1 = o1.get( "SOURCE_COLUMN_TYPE" );
            String columnType2 = o2.get( "SOURCE_COLUMN_TYPE" );

            int i = columnType1.compareTo( columnType2 );

            if ( i == 0 )
            {
                String columnName1 = o1.get( "SOURCE_COLUMN_NAME" );
                String columnName2 = o2.get( "SOURCE_COLUMN_NAME" );

                return columnName1.compareTo( columnName2 );
            }
            else
            {
                return i;
            }
        };
    }

    private Join buildJoin( JoinQueryInfo source, List<Map<String, String>> rows )
    {
        if ( rows.size() < 2 )
        {
            throw new IllegalStateException(
                    format( "No join exists between '%s' and '%s'", source.tableOne(), source.tableTwo() ) );
        }

        Predicate<Map<String, String>> primaryKeyPredicate =
                row -> row.get( "SOURCE_COLUMN_TYPE" ).equalsIgnoreCase( ColumnType.PrimaryKey.name() );
        Predicate<Map<String, String>> foreignKeyPredicate =
                row -> row.get( "SOURCE_COLUMN_TYPE" ).equalsIgnoreCase( ColumnType.ForeignKey.name() );

        JoinKey keyOne = null;
        JoinKey keyTwo = null;

        if ( rows.stream().anyMatch( primaryKeyPredicate ) )
        {
            keyOne = createJoinKey( rows.stream().filter( primaryKeyPredicate ).findFirst().get() );
            List<Map<String, String>> foreignKeys = rows.stream()
                    .filter( foreignKeyPredicate ).collect( Collectors.toList() );
            if ( foreignKeys.size() > 1 )
            {
                //Simple Join using composite Keys
                keyTwo = buildCompositeJoinKey( foreignKeys );
            }
            else
            {
                //Simple Join
                keyTwo = createJoinKey( foreignKeys.get( 0 ) );
            }
        }
        else
        {
            //Through a join table
            keyOne = createJoinKey( rows.get( 0 ) );
            keyTwo = createJoinKey( rows.get( 1 ) );
            /* Map<String, List<Map<String, String>>> groupByTargetTableName = rows.stream()
                    .collect( Collectors.groupingBy( r -> r.get( "TARGET_TABLE_NAME" ) ) );

            if (groupByTargetTableName.size() != 2)
            {
                throw new IllegalStateException( "Expected 2 target tables, found " + groupByTargetTableName.size() );
            }

            List<List<Map<String, String>>> values = new ArrayList<>( groupByTargetTableName.values() );
            keyOne = buildKey( values.get( 0 ) );
            keyTwo = buildKey( values.get( 1 ) );*/
        }

        return new Join( keyOne, keyTwo );
    }

    private JoinKey buildCompositeJoinKey( List<Map<String, String>> foreignKeys )
    {
        List<Column> columns1 = foreignKeys.stream()
                .map( this::sourceSimpleColumn ).collect( Collectors.toList() );
        List<Column> columns2 = foreignKeys.stream()
                .map( this::targetSimpleColumn ).collect( Collectors.toList() );

        Map<String, String> results = foreignKeys.get( 0 );
        TableName sourceTable = new TableName(
                results.get( "SOURCE_TABLE_SCHEMA" ),
                results.get( "SOURCE_TABLE_NAME" ) );
        TableName targetTable = new TableName(
                results.get( "TARGET_TABLE_SCHEMA" ),
                results.get( "TARGET_TABLE_NAME" ) );

        CompositeKeyColumn sourceCompositeKeyColumn = new CompositeKeyColumn( sourceTable, columns1 );
        CompositeKeyColumn targetCompositeKeyColumn = new CompositeKeyColumn( targetTable, columns2 );

        return new JoinKey( sourceCompositeKeyColumn, targetCompositeKeyColumn );
    }

    private Column sourceSimpleColumn( Map<String, String> results )
    {
        TableName sourceTable = new TableName(
                results.get( "SOURCE_TABLE_SCHEMA" ), results.get( "SOURCE_TABLE_NAME" ) );
        String sourceColumn = results.get( "SOURCE_COLUMN_NAME" );
        ColumnType sourceColumnType = ColumnType.valueOf( results.get( "SOURCE_COLUMN_TYPE" ) );
        return new SimpleColumn(
                sourceTable,
                sourceTable.fullyQualifiedColumnName( sourceColumn ),
                sourceColumn,
                sourceColumnType,
                SqlDataType.KEY_DATA_TYPE );
    }

    private JoinKey createJoinKey( Map<String, String> results )
    {
        return new JoinKey( sourceSimpleColumn( results ), targetSimpleColumn( results ) );
    }

    private SimpleColumn targetSimpleColumn( Map<String, String> results )
    {
        TableName targetTable = new TableName(
                results.get( "TARGET_TABLE_SCHEMA" ), results.get( "TARGET_TABLE_NAME" ) );
        ColumnType targetColumnType = ColumnType.valueOf( results.get( "TARGET_COLUMN_TYPE" ) );
        String targetColumn = results.get( "TARGET_COLUMN_NAME" );

        return new SimpleColumn(
                targetTable,
                targetTable.fullyQualifiedColumnName( targetColumn ),
                targetColumn,
                targetColumnType,
                SqlDataType.KEY_DATA_TYPE );
    }

    private String select( JoinQueryInfo source )
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
                " WHERE join_table.TABLE_SCHEMA = '" + source.table().schema() + "'" +
                " AND join_table.TABLE_NAME = '" + source.table().simpleName() + "'" +
                " AND " + source.specialisedSql() + " ;";
    }
}
