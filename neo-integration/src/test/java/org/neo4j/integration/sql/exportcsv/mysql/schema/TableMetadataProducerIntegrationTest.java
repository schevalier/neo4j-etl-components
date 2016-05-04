package org.neo4j.integration.sql.exportcsv.mysql.schema;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.LogManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Test;

import org.neo4j.integration.neo4j.importcsv.config.Delimiter;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.neo4j.importcsv.config.GraphObjectType;
import org.neo4j.integration.neo4j.importcsv.config.QuoteChar;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.DatabaseType;
import org.neo4j.integration.sql.exportcsv.ColumnUtil;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.exportcsv.mapping.CsvResource;
import org.neo4j.integration.sql.exportcsv.mapping.TableToCsvFieldMapper;
import org.neo4j.integration.sql.exportcsv.mysql.MySqlExportSqlSupplier;
import org.neo4j.integration.sql.metadata.ColumnRole;
import org.neo4j.integration.sql.metadata.SimpleColumn;
import org.neo4j.integration.sql.metadata.SqlDataType;
import org.neo4j.integration.sql.metadata.Table;
import org.neo4j.integration.sql.metadata.TableName;

import static org.junit.Assert.assertEquals;

public class TableMetadataProducerIntegrationTest
{
    private ColumnUtil columnUtil = new ColumnUtil();

    @Test
    public void testName() throws Exception
    {
        TableName table = new TableName( "ngsdb.l_release_group_url" );
        Formatting formatting = Formatting.builder()
                .delimiter( Delimiter.TAB )
                .quote( QuoteChar.TICK_QUOTES ).build();
        SimpleColumn label = new SimpleColumn(
                table,
                QuoteChar.DOUBLE_QUOTES.enquote( formatting.labelFormatter().format( table.simpleName() ) ),
                table.simpleName(),
                ColumnRole.Literal,
                SqlDataType.LABEL_DATA_TYPE );
        System.out.println( label.aliasedColumn() );

    }

    @Test
    public void shouldReturnJoinMetadataRelationshipCompositeKeyHavingPrimaryKeys() throws Exception
    {
        // given

        LogManager.getLogManager().readConfiguration( new FileInputStream( "/Users/praveena" +
                ".gunasekhar/projects/neo/neo-integration-root/neo-integration-cli/src/main/resources/debug-logging" +
                ".properties" ) );
        ConnectionConfig connectionConfig = ConnectionConfig.forDatabase( DatabaseType.MySQL )
                .host( "localhost" )
                .port( 3306 )
                .database( "ngsdb" )
                .username( "neo" )
                .password( "neo" )
                .build();

        DatabaseClient databaseClient = new DatabaseClient( connectionConfig );
//        DatabaseInspector databaseInspector = new DatabaseInspector( databaseClient );
//        SchemaExport schemaExport = databaseInspector.buildSchemaExport();
//        System.out.println(schemaExport);

        TableMetadataProducer tableMetadataProducer = new TableMetadataProducer( databaseClient );

        // when
        TableName releaseGroupUrl = new TableName( "ngsdb.l_release_release" );
        Collection<Table> table = tableMetadataProducer.createMetadataFor( releaseGroupUrl );

        // then
        ArrayList<Table> tables = new ArrayList<>( table );

        TableToCsvFieldMapper tableToCsvFieldMapper = new TableToCsvFieldMapper( Formatting.builder()
                .delimiter( Delimiter.TAB )
                .quote( QuoteChar.TICK_QUOTES ).build() );
        Table table1 = tables.get( 0 );
        ColumnToCsvFieldMappings mappings = tableToCsvFieldMapper.createMappings( table1 );
        CsvResource csvResource = new CsvResource( table1.descriptor(), GraphObjectType.Node,
                new MySqlExportSqlSupplier().sql( mappings ), mappings );
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        System.out.println( objectWriter.writeValueAsString( csvResource.toJson() ) );


//        assertEquals( table1.toString(), "" );
    }

}
