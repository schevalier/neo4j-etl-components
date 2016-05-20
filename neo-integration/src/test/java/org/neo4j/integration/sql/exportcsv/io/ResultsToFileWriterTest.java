package org.neo4j.integration.sql.exportcsv.io;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.Delimiter;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.GraphObjectType;
import org.neo4j.integration.sql.QueryResults;
import org.neo4j.integration.sql.StubQueryResults;
import org.neo4j.integration.sql.exportcsv.ColumnUtil;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.exportcsv.mapping.CsvResource;
import org.neo4j.integration.sql.metadata.ColumnRole;
import org.neo4j.integration.sql.metadata.ColumnValueSelectionStrategy;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.TableName;
import org.neo4j.integration.util.ResourceRule;
import org.neo4j.integration.util.TemporaryDirectory;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResultsToFileWriterTest
{
    @Rule
    public final ResourceRule<Path> tempDirectory = new ResourceRule<>( TemporaryDirectory.temporaryDirectory() );
    private ColumnUtil columnUtil = new ColumnUtil();
    private TableName table = new TableName( "users" );
    private final ColumnToCsvFieldMappings mappings = mock( ColumnToCsvFieldMappings.class );
    private Path exportFile = tempDirectory.get().resolve( "export-file.csv" );
    private static final Formatting TAB_DELIMITER = Formatting.builder().delimiter( Delimiter.TAB ).build();
    private final ResultsToFileWriter resultsToFileWriter = new ResultsToFileWriter( TAB_DELIMITER );

    @Test
    public void shouldCreateCsvFile() throws Exception
    {
        // given
        QueryResults results = StubQueryResults.builder()
                .columns( "id", "username" )
                .addRow( "1", "user-1" )
                .addRow( "2", "user-2" )
                .build();

        when( mappings.columns() ).thenReturn(
                asList(
                        columnUtil.keyColumn( table, "id", ColumnRole.PrimaryKey ),
                        columnUtil.column( table, "username", ColumnRole.Data ) ) );

        CsvResource resource = new CsvResource( table.fullName(), GraphObjectType.Node, "SELECT ...", mappings );

        // when
        resultsToFileWriter.write( results, exportFile, resource );

        // then
        List<String> contents = Files.readAllLines( exportFile );
        assertEquals( asList( "\"1\"\t\"user-1\"", "\"2\"\t\"user-2\"" ), contents );
    }

    @Test
    public void shouldWriteCompositeColumns() throws Exception
    {
        // given
        QueryResults results = StubQueryResults.builder()
                .columns( "first-name", "last-name" )
                .addRow( "Jane", "Smith" )
                .addRow( "John", "Smith" )
                .build();

        when( mappings.columns() ).thenReturn(
                Collections.singletonList(
                        columnUtil.compositeKeyColumn(
                                table, asList( "first-name", "last-name" ), ColumnRole.PrimaryKey ) ) );

        CsvResource resource = new CsvResource( table.fullName(), GraphObjectType.Node, "SELECT ...", mappings );

        // when
        resultsToFileWriter.write( results, exportFile, resource );

        // then
        List<String> contents = Files.readAllLines( exportFile );
        assertEquals( asList( "\"Jane\0Smith\"", "\"John\0Smith\"" ), contents );
    }

    @Test
    public void shouldNotAddQuotationForNonStringValues() throws Exception
    {
        // given
        QueryResults results = StubQueryResults.builder()
                .columns( "id", "username" )
                .addRow( "1", "user-1" )
                .addRow( "2", "user-2" )
                .build();

        when( mappings.columns() ).thenReturn(
                asList(
                        new SimpleColumn( table, "id", EnumSet.of( ColumnRole.Data ), SqlDataType.INT,
                                ColumnValueSelectionStrategy.SelectColumnValue ),
                        new SimpleColumn( table, "username", EnumSet.of( ColumnRole.Data ), SqlDataType.VARCHAR,
                                ColumnValueSelectionStrategy.SelectColumnValue ) ) );

        CsvResource resource = new CsvResource( table.fullName(), GraphObjectType.Node, "SELECT ...", mappings );

        // when
        resultsToFileWriter.write( results, exportFile, resource );

        // then
        List<String> contents = Files.readAllLines( exportFile );
        assertEquals( asList( "1\t\"user-1\"", "2\t\"user-2\"" ), contents );
    }

    @Test
    public void shouldWriteDelimiterForNullOrEmptyValues() throws Exception
    {
        // given

        QueryResults results = StubQueryResults.builder()
                .columns( "id", "username", "age" )
                .addRow( "1", "user-1", "45" )
                .addRow( "2", "", "32" )
                .addRow( "3", "user-3", null )
                .addRow( "4", "user-4", "" )
                .build();

        when( mappings.columns() ).thenReturn(
                asList(
                        columnUtil.keyColumn( table, "id", ColumnRole.PrimaryKey ),
                        columnUtil.column( table, "username", ColumnRole.Data ),
                        columnUtil.column( table, "age", ColumnRole.Data ) ) );

        CsvResource resource = new CsvResource( table.fullName(), GraphObjectType.Node, "SELECT ...", mappings );

        //when
        resultsToFileWriter.write( results, exportFile, resource );

        // then
        List<String> contents = Files.readAllLines( exportFile );
        assertEquals(
                asList( "\"1\"\t\"user-1\"\t\"45\"", "\"2\"\t\t\"32\"", "\"3\"\t\"user-3\"\t", "\"4\"\t\"user-4\"\t" ),
                contents );
    }

    @Test
    public void shouldSkipWritingRowForRelationshipsIfAnyColumnHasNullOrEmptyValues() throws Exception
    {
        // given
        QueryResults results = StubQueryResults.builder()
                .columns( "id", "username" )
                .addRow( "1", "user-1" )
                .addRow( "2", "" )
                .addRow( "3", null )
                .addRow( "", "user-4" )
                .build();

        when( mappings.columns() ).thenReturn(
                asList(
                        columnUtil.keyColumn( table, "id", ColumnRole.PrimaryKey ),
                        columnUtil.column( table, "username", ColumnRole.Data ) ) );

        CsvResource resource = new CsvResource(
                table.fullName(),
                GraphObjectType.Relationship,
                "SELECT ...",
                mappings );

        // when
        resultsToFileWriter.write( results, exportFile, resource );

        // then
        List<String> contents = Files.readAllLines( exportFile );
        assertEquals( asList( "\"1\"\t\"user-1\"", "\"2\"\t", "\"3\"\t" ), contents );
    }
}
