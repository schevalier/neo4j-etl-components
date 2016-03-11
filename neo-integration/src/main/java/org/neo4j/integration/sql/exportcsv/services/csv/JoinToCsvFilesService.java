package org.neo4j.integration.sql.exportcsv.services.csv;

import org.neo4j.integration.neo4j.importcsv.config.GraphObjectType;
import org.neo4j.integration.neo4j.importcsv.config.ManifestEntry;
import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.DatabaseObjectToCsvFilesService;
import org.neo4j.integration.sql.exportcsv.ExportToCsvConfig;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFilesWriter;
import org.neo4j.integration.sql.exportcsv.mapping.JoinToCsvFieldMapper;
import org.neo4j.integration.sql.metadata.Join;

class JoinToCsvFilesService implements DatabaseObjectToCsvFilesService
{
    private final Join join;

    public JoinToCsvFilesService( Join join )
    {
        this.join = join;
    }

    @Override
    public ManifestEntry exportToCsv( DatabaseExportSqlSupplier sqlSupplier,
                                      HeaderFileWriter headerFileWriter,
                                      CsvFileWriter csvFileWriter,
                                      ExportToCsvConfig config ) throws Exception
    {
        return new ManifestEntry( GraphObjectType.Relationship,
                new CsvFilesWriter<Join>( headerFileWriter, csvFileWriter )
                        .write( join, new JoinToCsvFieldMapper( config.formatting() ), sqlSupplier ) );
    }
}
