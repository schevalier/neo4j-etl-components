package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.Collection;

import org.junit.Test;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.sql.Results;
import org.neo4j.integration.sql.SqlRunner;
import org.neo4j.integration.sql.StubResults;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
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
        Results results = StubResults.builder()
                .columns( "COLUMN_NAME", "DATA_TYPE", "COLUMN_KEY" )
                .addRow( "id", "int", "PRI" )
                .addRow( "username", "text", "" )
                .addRow( "addressId", "int", "MUL" )
                .build();

        SqlRunner sqlRunner = mock( SqlRunner.class );
        when( sqlRunner.execute( any( String.class ) ) ).thenReturn( AwaitHandle.forReturnValue( results ) );

        TableMetadataProducer getTableMetadata = new TableMetadataProducer( sqlRunner );

        // when
        Collection<Table> metadata = getTableMetadata.createMetadataFor( new TableName( "test.Person" ) );

        // then
        TableName expectedTableName = new TableName( "test.Person" );
        Table table = metadata.stream().findFirst().get();

        assertEquals( expectedTableName, table.name() );
        assertEquals( "test.Person", table.descriptor());
        assertThat(table.columns(), contains(
                Column.builder()
                        .table( expectedTableName )
                        .name( expectedTableName.fullyQualifiedColumnName( "id" ) )
                        .alias( "id" )
                        .type( ColumnType.PrimaryKey )
                        .build(),
                Column.builder()
                        .table( expectedTableName )
                        .name( expectedTableName.fullyQualifiedColumnName( "username" ) )
                        .alias( "username" )
                        .type( ColumnType.Data )
                        .build(),
                Column.builder()
                        .table( expectedTableName )
                        .name( expectedTableName.fullyQualifiedColumnName( "addressId" ) )
                        .alias( "addressId" )
                        .type( ColumnType.ForeignKey )
                        .build() ));
    }
}
