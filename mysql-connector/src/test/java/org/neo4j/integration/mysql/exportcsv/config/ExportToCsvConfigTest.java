package org.neo4j.integration.mysql.exportcsv.config;

import java.nio.file.Paths;

import org.junit.Test;

import org.neo4j.integration.mysql.metadata.ColumnType;
import org.neo4j.integration.mysql.metadata.ConnectionConfig;
import org.neo4j.integration.mysql.metadata.Join;
import org.neo4j.integration.mysql.metadata.Table;
import org.neo4j.integration.mysql.metadata.TableName;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class ExportToCsvConfigTest
{
    @Test
    public void shouldThrowExceptionIfParentOfJoinIsNotPresentInTables()
    {
        try
        {
            // when
            ExportToCsvConfig.builder()
                    .destination( Paths.get( "" ) )
                    .connectionConfig( mock( ConnectionConfig.class ) )
                    .formatting( Formatting.DEFAULT )
                    .addTable( Table.builder()
                            .name( "test.Address" )
                            .addColumn( "id", ColumnType.PrimaryKey )
                            .addColumn( "postcode", ColumnType.Data )
                            .build() )
                    .addJoin( Join.builder()
                            .parentTable( new TableName( "test.Person" ) )
                            .primaryKey( "id" )
                            .foreignKey( "addressId" )
                            .childTable( new TableName( "test.Address" ) )
                            .build() )
                    .build();
            fail( "Expected IllegalStatException" );
        }
        catch ( IllegalStateException e )
        {
            // then
            assertEquals( "Config is missing table definition 'test.Person' for join [test.Person -> test.Address]",
                    e.getMessage() );
        }
    }

    @Test
    public void shouldThrowExceptionIfChildOfJoinIsNotPresentInTables()
    {
        try
        {
            // when
            ExportToCsvConfig.builder()
                    .destination( Paths.get( "" ) )
                    .connectionConfig( mock( ConnectionConfig.class ) )
                    .formatting( Formatting.DEFAULT )
                    .addTable( Table.builder()
                            .name( "test.Person" )
                            .addColumn( "id", ColumnType.PrimaryKey )
                            .addColumn( "username", ColumnType.Data )
                            .addColumn( "addressId", ColumnType.ForeignKey )
                            .build() )
                    .addJoin( Join.builder()
                            .parentTable( new TableName( "test.Person" ) )
                            .primaryKey( "id" )
                            .foreignKey( "addressId" )
                            .childTable( new TableName( "test.Address" ) )
                            .build() )
                    .build();
            fail( "Expected IllegalStatException" );
        }
        catch ( IllegalStateException e )
        {
            // then
            assertEquals( "Config is missing table definition 'test.Address' for join [test.Person -> test.Address]",
                    e.getMessage() );
        }
    }
}
