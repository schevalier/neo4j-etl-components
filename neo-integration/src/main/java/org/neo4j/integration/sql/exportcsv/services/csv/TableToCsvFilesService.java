package org.neo4j.integration.sql.exportcsv.services.csv;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.DatabaseObjectToCsvFilesService;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFilesWriter;
import org.neo4j.integration.sql.exportcsv.mapping.TableToCsvFieldMapper;
import org.neo4j.integration.sql.metadata.Table;

class TableToCsvFilesService implements DatabaseObjectToCsvFilesService
{
    private final Table table;

    public TableToCsvFilesService( Table table )
    {
        this.table = table;
    }

    @Override
    public Collection<Path> exportToCsv( DatabaseExportSqlSupplier sqlSupplier,
                                         HeaderFileWriter headerFileWriter,
                                         CsvFileWriter csvFileWriter,
                                         ExportToCsvConfig config ) throws Exception
    {
        return new CsvFilesWriter<Table>( headerFileWriter, csvFileWriter )
                .write( table, new TableToCsvFieldMapper( config.formatting() ), sqlSupplier );
    }
}
