package org.neo4j.integration.sql.exportcsv.services.csv;

import org.neo4j.integration.neo4j.importcsv.config.GraphObjectType;
import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.DatabaseObjectToCsvFilesService;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFiles;
import org.neo4j.integration.sql.exportcsv.io.CsvFilesWriter;
import org.neo4j.integration.sql.exportcsv.io.ManifestEntry;
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
    public ManifestEntry exportToCsv( DatabaseExportSqlSupplier sqlSupplier,
                                      HeaderFileWriter headerFileWriter,
                                      CsvFileWriter csvFileWriter,
                                      ExportToCsvConfig config ) throws Exception
    {
        CsvFiles csvFiles = new CsvFilesWriter<JoinTable>( headerFileWriter, csvFileWriter )
                .write( joinTable, new JoinTableToCsvFieldMapper( config.formatting() ), sqlSupplier );
        return new ManifestEntry( GraphObjectType.Relationship, csvFiles );
    }
}
