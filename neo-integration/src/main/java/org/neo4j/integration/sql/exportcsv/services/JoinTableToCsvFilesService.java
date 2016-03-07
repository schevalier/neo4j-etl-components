package org.neo4j.integration.sql.exportcsv.services;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.DatabaseObjectToCsvFilesService;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFilesWriter;
import org.neo4j.integration.sql.exportcsv.mapping.JoinTableToCsvFieldMapper;
import org.neo4j.integration.sql.metadata.JoinTable;

class JoinTableToCsvFilesService implements DatabaseObjectToCsvFilesService
{
    private final JoinTable joinTable;

    public JoinTableToCsvFilesService( JoinTable joinTable )
    {
        this.joinTable = joinTable;
    }

    @Override
    public Collection<Path> exportToCsv( DatabaseExportSqlSupplier sqlSupplier,
                                         HeaderFileWriter headerFileWriter,
                                         CsvFileWriter csvFileWriter,
                                         ExportToCsvConfig config ) throws Exception
    {
        return new CsvFilesWriter<JoinTable>( headerFileWriter, csvFileWriter )
                .write( joinTable, new JoinTableToCsvFieldMapper( config.formatting() ), sqlSupplier );
    }
}
