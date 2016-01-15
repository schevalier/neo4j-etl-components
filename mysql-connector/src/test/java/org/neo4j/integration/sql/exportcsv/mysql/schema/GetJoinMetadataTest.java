package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.sql.Results;
import org.neo4j.integration.sql.SqlRunner;
import org.neo4j.integration.sql.StubResults;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.TableName;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetJoinMetadataTest
{
    @Test
    public void shouldReturnJoinMetadata() throws Exception
    {
        // given
        Results results = StubResults.builder()
                .columns( "TABLE_SCHEMA",
                        "TABLE_NAME",
                        "PRIMARY_KEY",
                        "FOREIGN_KEY",
                        "REFERENCED_TABLE_SCHEMA",
                        "REFERENCED_TABLE_NAME" )
                .addRow( "test", "Person", "id", "addressId", "test", "Address" )
                .addRow( "test", "Address", "id", "ownerId", "test", "Person" )
                .build();

        SqlRunner sqlRunner = mock( SqlRunner.class );
        when( sqlRunner.execute( any( String.class ) ) ).thenReturn( AwaitHandle.forReturnValue( results ) );

        GetJoinMetadata getJoinMetadata = new GetJoinMetadata( sqlRunner );

        // when
        Collection<Join> joins = getJoinMetadata
                .getMetadataFor( new TableName( "test.Person" ), new TableName( "test.Address" ) );

        // then
        Iterator<Join> iterator = joins.iterator();

        Join join1 = iterator.next();

        assertEquals( Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "id" )
                .type( ColumnType.PrimaryKey )
                .build(), join1.primaryKey() );

        assertEquals( Column.builder()
                .table( new TableName( "test.Person" ) )
                .name( "addressId" )
                .type( ColumnType.ForeignKey )
                .build(), join1.foreignKey() );

        assertEquals( new TableName( "test.Address" ), join1.childTable() );

        Join join2 = iterator.next();

        assertEquals( Column.builder()
                .table( new TableName( "test.Address" ) )
                .name( "id" )
                .type( ColumnType.PrimaryKey )
                .build(), join2.primaryKey() );

        assertEquals( Column.builder()
                .table( new TableName( "test.Address" ) )
                .name( "ownerId" )
                .type( ColumnType.ForeignKey )
                .build(), join2.foreignKey() );

        assertEquals( new TableName( "test.Person" ), join2.childTable() );

        assertFalse( iterator.hasNext() );
    }
}
