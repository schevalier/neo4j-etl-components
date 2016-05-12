package org.neo4j.integration.sql.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.StubQueryResults;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KeyCollectionAssemblerTest
{
    @Test
    public void shouldReturnKeyCollectionWithPrimaryKey() throws Exception
    {
        // given
        DatabaseClient databaseClient = new DatabaseClientBuilder().setPrimaryKey( "id" ).build();

        KeyCollectionAssembler assembler = new KeyCollectionAssembler( databaseClient );

        // when
        KeyCollection keyCollection = assembler.createKeyCollection( new TableName( "javabase.Example" ) );

        // then
        assertTrue( keyCollection.primaryKey().isPresent() );
        assertTrue( keyCollection.foreignKeys().isEmpty() );
        assertFalse( keyCollection.representsJoinTable() );

        assertEquals( "javabase.Example.id", keyCollection.primaryKey().orElseGet( () -> null ).name() );
    }

    @Test
    public void shouldReturnKeyCollectionWithCompositePrimaryKey() throws Exception
    {
        // given
        DatabaseClient databaseClient = new DatabaseClientBuilder().setPrimaryKey( "first_name", "last_name" ).build();

        KeyCollectionAssembler assembler = new KeyCollectionAssembler( databaseClient );

        // when
        KeyCollection keyCollection = assembler.createKeyCollection( new TableName( "javabase.Example" ) );

        // then
        assertTrue( keyCollection.primaryKey().isPresent() );
        assertTrue( keyCollection.foreignKeys().isEmpty() );
        assertFalse( keyCollection.representsJoinTable() );

        assertEquals( join( "javabase.Example.first_name", "javabase.Example.last_name" ),
                keyCollection.primaryKey().orElseGet( () -> null ).name() );
    }

    @Test
    public void shouldReturnKeyCollectionWithTwoForeignKeys() throws Exception
    {
        // given
        DatabaseClient databaseClient = new DatabaseClientBuilder()
                .addForeignKey( "author_id" )
                .addForeignKey( "book_id" )
                .build();

        KeyCollectionAssembler assembler = new KeyCollectionAssembler( databaseClient );

        // when
        KeyCollection keyCollection = assembler.createKeyCollection( new TableName( "javabase.Example" ) );

        // then
        assertFalse( keyCollection.primaryKey().isPresent() );
        assertEquals( 2, keyCollection.foreignKeys().size() );
        assertTrue( keyCollection.representsJoinTable() );

        assertThat(
                keyCollection.foreignKeys().stream().map( k -> k.sourceColumn().name() ).collect( Collectors.toList() ),
                hasItems( "javabase.Example.author_id", "javabase.Example.book_id" ) );
    }

    @Test
    public void shouldReturnKeyCollectionWithTwoCompositeForeignKeys() throws Exception
    {
        // given
        DatabaseClient databaseClient = new DatabaseClientBuilder()
                .addForeignKey( "column_1", "column_2" )
                .addForeignKey( "column_3", "column_4" )
                .build();

        KeyCollectionAssembler assembler = new KeyCollectionAssembler( databaseClient );

        // when
        KeyCollection keyCollection = assembler.createKeyCollection( new TableName( "javabase.Example" ) );

        // then
        assertFalse( keyCollection.primaryKey().isPresent() );
        assertEquals( 2, keyCollection.foreignKeys().size() );
        assertTrue( keyCollection.representsJoinTable() );

        assertThat(
                keyCollection.foreignKeys().stream().map( k -> k.sourceColumn().name() ).collect( Collectors.toList() ),
                hasItems( join( "javabase.Example.column_1", "javabase.Example.column_2" ),
                        join( "javabase.Example.column_3", "javabase.Example.column_4" ) ) );
    }

    private String join( String... columns )
    {
        return StringUtils.join( columns, CompositeColumn.SEPARATOR );
    }

    private static class DatabaseClientBuilder
    {
        private final DatabaseClient databaseClient = mock( DatabaseClient.class );
        private final Collection<String> columns = new HashSet<>();
        private final List<String> primaryKey = new ArrayList<>();
        private final List<List<String>> foreignKeys = new ArrayList<>();

        DatabaseClientBuilder setPrimaryKey( String... primaryKeyElement )
        {
            List<String> elements = asList( primaryKeyElement );

            this.columns.addAll( elements );
            this.primaryKey.addAll( elements );

            return this;
        }

        DatabaseClientBuilder addForeignKey( String... foreignKeyElement )
        {
            List<String> elements = asList( foreignKeyElement );

            this.columns.addAll( elements );
            this.foreignKeys.add( elements );

            return this;
        }

        public DatabaseClient build() throws Exception

        {
            StubQueryResults.Builder columnsResults = StubQueryResults.builder().columns( "COLUMN_NAME", "TYPE_NAME" );
            columns.forEach( c -> columnsResults.addRow( c, "INT" ) );

            StubQueryResults.Builder primaryKeyResults = StubQueryResults.builder().columns( "COLUMN_NAME" );
            primaryKey.forEach( primaryKeyResults::addRow );

            StubQueryResults.Builder foreignKeyResults = StubQueryResults.builder()
                    .columns( "FK_NAME", "FKCOLUMN_NAME", "PKTABLE_SCHEM", "PKTABLE_NAME", "PKCOLUMN_NAME" );
            for ( int i = 0; i < foreignKeys.size(); i++ )
            {
                List<String> foreignKeyElements = foreignKeys.get( i );
                String fkName = format( "fk_%s", i );
                foreignKeyElements.forEach( fk -> foreignKeyResults.addRow( fkName, fk, "javabase", "Example", "id" ) );
            }

            when( databaseClient.columns( any() ) ).thenReturn( columnsResults.build() );
            when( databaseClient.primaryKeys( any() ) ).thenReturn( primaryKeyResults.build() );
            when( databaseClient.foreignKeys( any() ) ).thenReturn( foreignKeyResults.build() );

            return databaseClient;
        }

    }
}
