package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Paths;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;

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
            TableName addressTable = new TableName( "test.Address" );

            ExportToCsvConfig.builder()
                    .destination( Paths.get( "" ) )
                    .connectionConfig( mock( ConnectionConfig.class ) )
                    .formatting( Formatting.DEFAULT )
                    .addTable( Table.builder()
                            .name( addressTable )
                            .addColumn( column( addressTable, "id", ColumnType.PrimaryKey ) )
                            .addColumn( column( addressTable, "postcode", ColumnType.Data ) )
                            .build() )
                    .addJoin( Join.builder()
                            .parentTable( new TableName( "test.Person" ) )
                            .primaryKey( "id" )
                            .foreignKey( "addressId" )
                            .childTable( addressTable )
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
            TableName personTable = new TableName( "test.Person" );

            ExportToCsvConfig.builder()
                    .destination( Paths.get( "" ) )
                    .connectionConfig( mock( ConnectionConfig.class ) )
                    .formatting( Formatting.DEFAULT )
                    .addTable( Table.builder()
                            .name( personTable )
                            .addColumn( column( personTable, "id", ColumnType.PrimaryKey ) )
                            .addColumn( column( personTable, "username", ColumnType.Data ) )
                            .addColumn( column( personTable, "addressId", ColumnType.ForeignKey ) )
                            .build() )
                    .addJoin( Join.builder()
                            .parentTable( personTable )
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

    private Column column( TableName table, String name, ColumnType type )
    {
        return Column.builder()
                .table( table )
                .name( table.fullyQualifiedColumnName( name ) )
                .alias( name )
                .type( type )
                .build();
    }
}
