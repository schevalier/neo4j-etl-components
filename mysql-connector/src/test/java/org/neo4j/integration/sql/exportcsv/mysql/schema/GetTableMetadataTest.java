package org.neo4j.integration.sql.exportcsv.mysql.schema;

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

public class GetTableMetadataTest
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

        GetTableMetadata getTableMetadata = new GetTableMetadata( sqlRunner );

        // when
        Table metadata = getTableMetadata.getMetadataFor( new TableName( "test.Person" ) );

        // then
        TableName expectedTableName = new TableName( "test.Person" );

        assertEquals( expectedTableName, metadata.name());
        assertEquals( "test.Person", metadata.descriptor());
        assertThat(metadata.columns(), contains(
                Column.builder()
                        .table( expectedTableName )
                        .name( "id" ).type(ColumnType.PrimaryKey )
                        .build(),
                Column.builder()
                        .table( expectedTableName )
                        .name( "username" ).type(ColumnType.Data )
                        .build(),
                Column.builder()
                        .table( expectedTableName )
                        .name( "addressId" ).type(ColumnType.ForeignKey )
                        .build()));
    }
}
