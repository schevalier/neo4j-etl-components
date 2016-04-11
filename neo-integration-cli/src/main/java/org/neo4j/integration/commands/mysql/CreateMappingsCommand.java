package org.neo4j.integration.commands.mysql;

import org.neo4j.integration.commands.DatabaseInspector;
import org.neo4j.integration.commands.SchemaExport;
import org.neo4j.integration.neo4j.importcsv.config.Formatting;
import org.neo4j.integration.sql.ConnectionConfig;
import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.mapping.CsvResources;
import org.neo4j.integration.util.CliRunner;

public class CreateMappingsCommand
{
    private final ConnectionConfig connectionConfig;
    private final Formatting formatting;
    private final DatabaseExportSqlSupplier sqlSupplier;

    public CreateMappingsCommand( ConnectionConfig connectionConfig,
                                  Formatting formatting,
                                  DatabaseExportSqlSupplier sqlSupplier )
    {
        this.connectionConfig = connectionConfig;
        this.formatting = formatting;
        this.sqlSupplier = sqlSupplier;
    }

    public void execute() throws Exception
    {
        CliRunner.print( "Creating MySQL to CSV mappings..." );

        SchemaExport schemaExport = new DatabaseInspector( new DatabaseClient( connectionConfig ) ).buildSchemaExport();
        CsvResources csvResources = schemaExport.createCsvResources( formatting, sqlSupplier );

        CliRunner.print( "Done" );
    }
}
