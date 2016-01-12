package org.neo4j.mysql.config;

import java.nio.file.Paths;

import org.junit.Test;

import org.neo4j.ingest.config.CsvField;
import org.neo4j.ingest.config.Formatting;
import org.neo4j.ingest.config.QuoteChar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class RelationalDatabaseExportConfigTest
{
    @Test
    public void shouldThrowExceptionIfParentOfJoinIsNotPresentInTables()
    {
        try
        {
            // when
            RelationalDatabaseExportConfig.builder()
                    .destination( Paths.get( "" ) )
                    .connectionConfig( mock( ConnectionConfig.class ) )
                    .formatting( Formatting.DEFAULT )
                    .addTable( Table.builder()
                            .name( "test.Address" )
                            .id( "id" )
                            .addColumn( "postcode", CsvField.data( "postcode" ) )
                            .build() )
                    .addJoin( Join.builder()
                            .parent( new TableName( "test.Person" ), "addressId" )
                            .child( new TableName( "test.Address" ), "id" )
                            .quoteCharacter( QuoteChar.SINGLE_QUOTES )
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
            RelationalDatabaseExportConfig.builder()
                    .destination( Paths.get( "" ) )
                    .connectionConfig( mock( ConnectionConfig.class ) )
                    .formatting( Formatting.DEFAULT )
                    .addTable( Table.builder()
                            .name( "test.Person" )
                            .id( "id" )
                            .addColumn( "name", CsvField.data( "name" ) )
                            .build() )
                    .addJoin( Join.builder()
                            .parent( new TableName( "test.Person" ), "addressId" )
                            .child( new TableName( "test.Address" ), "id" )
                            .quoteCharacter( QuoteChar.SINGLE_QUOTES )
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
