package org.neo4j.integration.sql.exportcsv.mysql;

import org.neo4j.integration.sql.DatabaseClient;
import org.neo4j.integration.sql.exportcsv.DatabaseExportService;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;

public class MySqlExportService implements DatabaseExportService
{
    private final MySqlExportSqlSupplier sqlSupplier;

    public MySqlExportService()
    {
        this.sqlSupplier = new MySqlExportSqlSupplier();
    }

    @Override
    public CsvFileWriter createExportFileWriter( ExportToCsvConfig config, DatabaseClient databaseClient )
    {
        return new CsvFileWriter( config, databaseClient );
    }

    @Override
    public DatabaseExportSqlSupplier sqlSupplier()
    {
        return sqlSupplier;
    }
}
