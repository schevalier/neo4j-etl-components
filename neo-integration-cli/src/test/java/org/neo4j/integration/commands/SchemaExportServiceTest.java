package org.neo4j.integration.commands;

import java.util.Optional;

import org.junit.Test;

public class SchemaExportServiceTest
{

    private SchemaExportService schemaExportService = new SchemaExportService();

    @Test
    public void exportSchemaShouldExportTablesAndJoins() throws Exception
    {
        // given
        schemaExportService.doExport( null, new SchemaDetails( "javabase", "Person", "Address", Optional.empty() ) );

        // when

        // then
    }
}
