package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.Collection;

import org.junit.Test;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.StubQueryResults;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlDataType;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TableMetadataProducerTest
{
    @Test
    public void shouldReturnTableMetadata() throws Exception
    {
        // given
        QueryResults results = StubQueryResults.builder()
                .columns( "COLUMN_NAME", "DATA_TYPE", "COLUMN_TYPE" )
                .addRow( "id", "INT", "PrimaryKey" )
                .addRow( "username", "TEXT", "Data" )
                .addRow( "addressId", "int", "ForeignKey" )
                .build();

        DatabaseClient databaseClient = mock( DatabaseClient.class );
        when( databaseClient.executeQuery( any( String.class ) ) ).thenReturn( AwaitHandle.forReturnValue( results ) );

        TableMetadataProducer getTableMetadata = new TableMetadataProducer( databaseClient );

        // when
        Collection<Table> metadata = getTableMetadata.createMetadataFor( new TableName( "test.Person" ) );

        // then
        TableName expectedTableName = new TableName( "test.Person" );
        Table table = metadata.stream().findFirst().get();

        assertEquals( expectedTableName, table.name() );
        assertEquals( "test.Person", table.descriptor() );

        assertThat( table.columns(), contains(
                new SimpleColumn(
                        expectedTableName,
                        expectedTableName.fullyQualifiedColumnName( "id" ),
                        "id",
                        ColumnType.PrimaryKey,
                        MySqlDataType.INT ),
                new SimpleColumn(
                        expectedTableName,
                        expectedTableName.fullyQualifiedColumnName( "username" ),
                        "username",
                        ColumnType.Data,
                        MySqlDataType.TEXT ),
                new SimpleColumn(
                        expectedTableName,
                        expectedTableName.fullyQualifiedColumnName( "addressId" ),
                        "addressId",
                        ColumnType.ForeignKey,
                        MySqlDataType.INT ) ) );
    }
}
