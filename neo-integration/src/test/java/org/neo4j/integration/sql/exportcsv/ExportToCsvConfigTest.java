package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Paths;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlDataType;
import org.neo4j.integration.sql.metadata.Column;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.SimpleColumn;
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
            TableName leftTable = new TableName( "test.Person" );
            TableName rightTable = new TableName( "test.Address" );

            ExportToCsvConfig.builder()
                    .destination( Paths.get( "" ) )
                    .connectionConfig( mock( ConnectionConfig.class ) )
                    .formatting( Formatting.DEFAULT )
                    .addTable( Table.builder()
                            .name( rightTable )
                            .addColumn( column( rightTable, "id", ColumnType.PrimaryKey ) )
                            .addColumn( column( rightTable, "postcode", ColumnType.Data ) )
                            .build() )
                    .addJoin( Join.builder()
                            .leftSource( leftTable, "id", ColumnType.PrimaryKey )
                            .leftTarget( leftTable, "id", ColumnType.PrimaryKey )
                            .rightSource( leftTable, "addressId", ColumnType.ForeignKey )
                            .rightTarget( rightTable, "id", ColumnType.PrimaryKey )
                            .startTable( leftTable )
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
            TableName leftTable = new TableName( "test.Person" );
            TableName rightTable = new TableName( "test.Address" );

            ExportToCsvConfig.builder()
                    .destination( Paths.get( "" ) )
                    .connectionConfig( mock( ConnectionConfig.class ) )
                    .formatting( Formatting.DEFAULT )
                    .addTable( Table.builder()
                            .name( leftTable )
                            .addColumn( column( leftTable, "id", ColumnType.PrimaryKey ) )
                            .addColumn( column( leftTable, "username", ColumnType.Data ) )
                            .addColumn( column( leftTable, "addressId", ColumnType.ForeignKey ) )
                            .build() )
                    .addJoin( Join.builder()
                            .leftSource( leftTable, "id", ColumnType.PrimaryKey )
                            .leftTarget( leftTable, "id", ColumnType.PrimaryKey )
                            .rightSource( leftTable, "addressId", ColumnType.ForeignKey )
                            .rightTarget( rightTable, "id", ColumnType.PrimaryKey )
                            .startTable( leftTable )
                            .build() )
                    .build();
            fail( "Expected IllegalStateException" );
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
        return new SimpleColumn(
                table,
                table.fullyQualifiedColumnName( name ),
                name,
                type,
                MySqlDataType.TEXT );
    }
}
