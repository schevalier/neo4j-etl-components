package org.neo4j.integration.sql.exportcsv;

import java.util.Collection;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import org.neo4j.integration.io.AwaitHandle;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.StubQueryResults;
import org.neo4j.integration.sql.exportcsv.mysql.schema.JoinMetadataProducer;
import org.neo4j.integration.sql.exportcsv.mysql.schema.JoinTableMetadataProducer;
import org.neo4j.integration.sql.exportcsv.mysql.schema.TableMetadataProducer;
import org.neo4j.integration.sql.metadata.Join;
import org.neo4j.integration.sql.metadata.JoinTable;
import org.neo4j.integration.sql.metadata.JoinTableInfo;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.sql.metadata.TableNamePair;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DatabaseExportTest
{

    private TableMetadataProducer tableMetadataProducer = mock( TableMetadataProducer.class );
    private JoinMetadataProducer joinMetadataProducer = mock( JoinMetadataProducer.class );
    private JoinTableMetadataProducer joinTableMetadataProducer = mock( JoinTableMetadataProducer.class );
    private DatabaseClient databaseClient = mock( DatabaseClient.class );
    private DatabaseExport databaseExport = new DatabaseExport(
            tableMetadataProducer,
            joinMetadataProducer,
            joinTableMetadataProducer,
            databaseClient );

    @Test
    public void shouldAddTableToConfigForTableWithPrimaryKeyAndNoForeignKeys() throws Exception
    {
        // given
        ExportToCsvConfig.Builder config = mock( ExportToCsvConfig.Builder.class );

        TableName address = new TableName( "test.Address" );
        Collection<Table> expectedTables = singletonList( mock( Table.class ) );

        QueryResults results = StubQueryResults.builder()
                .columns( "COLUMN_NAME", "REFERENCED_TABLE_NAME", "REFERENCED_COLUMN_NAME", "COLUMN_KEY" )
                .addRow( "id", null, null, "PRI" )
                .build();

        when( tableMetadataProducer.createMetadataFor( address ) ).thenReturn( expectedTables );
        when( databaseClient.executeQuery( anyString() ) ).thenReturn( AwaitHandle.forReturnValue( results ) );

        // when
        databaseExport.addTableToConfig( config, address );

        // then
        verify( config ).addTables( argThat( matchesCollection( expectedTables ) ) );
        verify( config ).addJoins( argThat( matchesCollection( emptyList() ) ) );
        ;
    }

    @Test
    public void shouldAddTableAndJoinToConfigForTableWithPrimaryKeyAndForeignKey() throws Exception
    {
        // given
        ExportToCsvConfig.Builder config = mock( ExportToCsvConfig.Builder.class );

        Table personTable = mock( Table.class );
        List<Table> expectedTables = singletonList( personTable );
        List<Join> expectedJoins = singletonList( mock( Join.class ) );

        TableName person = new TableName( "test.Person" );
        TableName address = new TableName( "test.Address" );

        QueryResults results = StubQueryResults.builder()
                .columns( "COLUMN_NAME", "REFERENCED_TABLE_NAME", "REFERENCED_COLUMN_NAME", "COLUMN_KEY" )
                .addRow( "id", null, null, "PRI" )
                .addRow( "addressId", "Address", "id", "MUL" )
                .build();

        when( tableMetadataProducer.createMetadataFor( person ) ).thenReturn( expectedTables );
        when( joinMetadataProducer.createMetadataFor( new TableNamePair( person, address ) ) )
                .thenReturn( expectedJoins );
        when( databaseClient.executeQuery( anyString() ) ).thenReturn( AwaitHandle.forReturnValue( results ) );

        // when
        databaseExport.addTableToConfig( config, person );

        // then
        verify( config ).addTables( argThat( matchesCollection( expectedTables ) ) );
        verify( config ).addJoins( argThat( matchesCollection( expectedJoins ) ) );
        verify( config ).addJoinTables( argThat( matchesCollection( emptyList() ) ) );
    }

    @Test
    public void shouldAddJoinTableForTableWithTwoForeignKeysAndNoPrimaryKey() throws Exception
    {
        // given
        ExportToCsvConfig.Builder config = mock( ExportToCsvConfig.Builder.class );

        TableName joinTable = new TableName( "test.Student_Course" );
        TableName student = new TableName( "test.Student" );
        TableName course = new TableName( "test.Course" );
        TableNamePair referencedTables = new TableNamePair( student, course );
        Collection<JoinTable> joinTables = singletonList( mock( JoinTable.class ) );

        QueryResults results = StubQueryResults.builder()
                .columns( "COLUMN_NAME", "REFERENCED_TABLE_NAME", "REFERENCED_COLUMN_NAME", "COLUMN_KEY" )
                .addRow( "studentId", "Student", "id", "MUL" )
                .addRow( "courseId", "Course", "id", "MUL" )
                .build();

        when( joinTableMetadataProducer.createMetadataFor( new JoinTableInfo( joinTable, referencedTables ) ) )
                .thenReturn( joinTables );
        when( databaseClient.executeQuery( anyString() ) ).thenReturn( AwaitHandle.forReturnValue( results ) );

        // when
        databaseExport.addTableToConfig( config, joinTable );

        // then
        verify( config ).addJoinTables( argThat( matchesCollection( joinTables ) ) );

        verify( config ).addTables( argThat( matchesCollection( emptyList() ) ) );
        verify( config ).addJoins( argThat( matchesCollection( emptyList() ) ) );
    }

    @Test
    public void shouldAddJoinTableForTableWithCandidateKey() throws Exception
    {
        // given
        ExportToCsvConfig.Builder config = mock( ExportToCsvConfig.Builder.class );

        TableName joinTable = new TableName( "test.Student_Course" );
        TableName student = new TableName( "test.Student" );
        TableName course = new TableName( "test.Course" );
        TableNamePair referencedTables = new TableNamePair( student, course );
        Collection<JoinTable> joinTables = singletonList( mock( JoinTable.class ) );

        QueryResults results = StubQueryResults.builder()
                .columns( "COLUMN_NAME", "REFERENCED_TABLE_NAME", "REFERENCED_COLUMN_NAME", "COLUMN_KEY" )
                .addRow( "studentId", "Student", "id", "PRI" )
                .addRow( "courseId", "Course", "id", "PRI" )
                .build();

        when( joinTableMetadataProducer.createMetadataFor( new JoinTableInfo( joinTable, referencedTables ) ) )
                .thenReturn( joinTables );
        when( databaseClient.executeQuery( anyString() ) ).thenReturn( AwaitHandle.forReturnValue( results ) );

        // when
        databaseExport.addTableToConfig( config, joinTable );

        // then
        verify( config ).addJoinTables( argThat( matchesCollection( joinTables ) ) );

        verify( config ).addTables( argThat( matchesCollection( emptyList() ) ) );
        verify( config ).addJoins( argThat( matchesCollection( emptyList() ) ) );
    }

    private <T> TypeSafeMatcher<Collection<T>> matchesCollection( final Collection<T> expected )
    {
        return new TypeSafeMatcher<Collection<T>>()
        {
            @Override
            protected boolean matchesSafely( Collection<T> tables )
            {
                return tables.containsAll( expected ) && expected.containsAll( tables );
            }

            @Override
            public void describeTo( Description description )
            {
                description.appendText( expected.toString() );
            }
        };
    }
}
