package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.JoinTableInfo;
import org.neo4j.integration.sql.metadata.MetadataProducer;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.TableName;

public class JoinTableMetadataProducer implements MetadataProducer<JoinTableInfo, JoinTable>
{
    private DatabaseClient databaseClient;

    public JoinTableMetadataProducer( DatabaseClient databaseClient )
    {
        this.databaseClient = databaseClient;
    }

    @Override
    public Collection<JoinTable> createMetadataFor( JoinTableInfo source ) throws Exception
    {
        String sql = select( source.joinTableName(), source.referencedTables().startTable() ) +
                " UNION " +
                select( source.joinTableName(), source.referencedTables().endTable() );

        Collection<JoinTable> joinTables = new ArrayList<>();

        try ( QueryResults results = databaseClient.executeQuery( sql ).await() )
        {

            ColumnPair start = getColumnPair( results, source, jT -> jT.referencedTables().startTable() );
            ColumnPair end = getColumnPair( results, source, jT -> jT.referencedTables().endTable() );

            JoinTable joinTable = JoinTable.builder()
                    .startForeignKey( start.foreignKey() )
                    .connectsToStartTablePrimaryKey( start.referencedPrimaryKey() )
                    .endForeignKey( end.foreignKey() )
                    .connectsToEndTablePrimaryKey( end.referencedPrimaryKey() )
                    .build();


            joinTables.add( joinTable );
        }

        return joinTables;

    }

    private ColumnPair getColumnPair( QueryResults results, JoinTableInfo joinTableInfo, Function<JoinTableInfo,
            TableName> referenceTableFunction ) throws Exception
    {
        results.next();
        TableName joinTableName = joinTableInfo.joinTableName();
        TableName referencedTable = referenceTableFunction.apply( joinTableInfo );
        return new ColumnPair(
                Column.builder()
                        .table( joinTableName )
                        .name( joinTableName.fullyQualifiedColumnName( results.getString( "FOREIGN_KEY" ) ) )
                        .alias( results.getString( "FOREIGN_KEY" ) )
                        .columnType( ColumnType.ForeignKey )
                        .dataType( SqlDataType.KEY_DATA_TYPE )
                        .build(),
                Column.builder()
                        .table( referencedTable )
                        .name( referencedTable.fullyQualifiedColumnName(   results.getString( "REFERENCED_PRIMARY_KEY" ) ))
                        .alias( results.getString( "REFERENCED_PRIMARY_KEY" ) )
                        .columnType( ColumnType.PrimaryKey )
                        .dataType( SqlDataType.KEY_DATA_TYPE )
                        .build()

        );
    }


    private static class ColumnPair
    {
        private final Column foreignKey;
        private final Column referencedPrimaryKey;

        private ColumnPair( Column foreignKey, Column referencedPrimaryKey )
        {
            this.foreignKey = foreignKey;
            this.referencedPrimaryKey = referencedPrimaryKey;
        }

        public Column foreignKey()
        {
            return foreignKey;
        }

        public Column referencedPrimaryKey()
        {
            return referencedPrimaryKey;
        }
    }

    private String select( TableName joinTable, TableName referenceTable )
    {
        return "SELECT " +
                " kcu.TABLE_SCHEMA," +
                " kcu.TABLE_NAME," +
                " kcu.COLUMN_NAME AS FOREIGN_KEY," +
                " c1.DATA_TYPE AS COLUMN_DATA_TYPE," +
                " kcu.REFERENCED_COLUMN_NAME AS REFERENCED_PRIMARY_KEY," +
                " c2.DATA_TYPE AS REFERENCED_COLUMN_DATA_TYPE," +
                " kcu.REFERENCED_TABLE_SCHEMA," +
                " kcu.REFERENCED_TABLE_NAME " +
                "FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE AS kcu " +
                "INNER JOIN INFORMATION_SCHEMA.COLUMNS AS c1 ON " +
                " (kcu.TABLE_SCHEMA = c1.TABLE_SCHEMA AND " +
                " kcu.TABLE_NAME = c1.TABLE_NAME " +
                " AND kcu.COLUMN_NAME = c1.COLUMN_NAME) " +
                "INNER JOIN INFORMATION_SCHEMA.COLUMNS AS c2 ON " +
                " (kcu.TABLE_SCHEMA = c2.TABLE_SCHEMA " +
                " AND kcu.TABLE_NAME = c2.TABLE_NAME " +
                " AND kcu.COLUMN_NAME = c2.COLUMN_NAME) " +
                "INNER JOIN INFORMATION_SCHEMA.COLUMNS AS c3 ON " +
                " (kcu.TABLE_SCHEMA = c3.TABLE_SCHEMA " +
                " AND kcu.TABLE_NAME = c3.TABLE_NAME " +
                " AND c3.COLUMN_KEY = 'MUL') " +
                "WHERE kcu.TABLE_SCHEMA = '" + joinTable.schema() + "' " +
                " AND kcu.TABLE_NAME = '" + joinTable.simpleName() + "' " +
                " AND kcu.REFERENCED_TABLE_SCHEMA = '" + referenceTable.schema() + "' " +
                " AND kcu.REFERENCED_TABLE_NAME = '" + referenceTable.simpleName() + "'";
    }
}
