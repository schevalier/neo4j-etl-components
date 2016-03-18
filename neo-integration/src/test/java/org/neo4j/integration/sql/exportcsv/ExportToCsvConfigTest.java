package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Paths;

import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.metadata.ColumnType;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinKey;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class ExportToCsvConfigTest
{
    private final TestUtil testUtil = new TestUtil();

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
                            .addColumn( testUtil.column( rightTable, "id", ColumnType.PrimaryKey ) )
                            .addColumn( testUtil.column( rightTable, "postcode", ColumnType.Data ) )
                            .build() )
                    .addJoin( new Join(
                            new JoinKey(
                                    testUtil.column( leftTable, "id", ColumnType.PrimaryKey ),
                                    testUtil.column( leftTable, "id", ColumnType.PrimaryKey ) ),
                            new JoinKey(
                                    testUtil.column( leftTable, "addressId", ColumnType.ForeignKey ),
                                    testUtil.column( rightTable, "id", ColumnType.PrimaryKey ) )
                    ) )
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
                            .addColumn( testUtil.column( leftTable, "id", ColumnType.PrimaryKey ) )
                            .addColumn( testUtil.column( leftTable, "username", ColumnType.Data ) )
                            .addColumn( testUtil.column( leftTable, "addressId", ColumnType.ForeignKey ) )
                            .build() )
                    .addJoin( new Join(
                            new JoinKey(
                                    testUtil.column( leftTable, "id", ColumnType.PrimaryKey ),
                                    testUtil.column( leftTable, "id", ColumnType.PrimaryKey ) ),
                            new JoinKey(
                                    testUtil.column( leftTable, "addressId", ColumnType.ForeignKey ),
                                    testUtil.column( rightTable, "id", ColumnType.PrimaryKey ) )
                    ) )
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
}
