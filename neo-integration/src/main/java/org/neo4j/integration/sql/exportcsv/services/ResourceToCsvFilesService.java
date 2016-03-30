package org.neo4j.integration.sql.exportcsv.services;

import java.nio.file.Path;

import org.neo4j.integration.neo4j.importcsv.config.CsvFiles;
import org.neo4j.integration.neo4j.importcsv.config.ManifestEntry;
import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.mapping.CsvResource;

public class ResourceToCsvFilesService
{
    private final HeaderFileWriter headerFileWriter;
    private final CsvFileWriter csvFileWriter;

    public ResourceToCsvFilesService( HeaderFileWriter headerFileWriter, CsvFileWriter csvFileWriter )
    {
        this.headerFileWriter = headerFileWriter;
        this.csvFileWriter = csvFileWriter;
    }

    public ManifestEntry exportToCsv( CsvResource resource ) throws Exception
    {
        Path headerFile = headerFileWriter.writeHeaderFile( resource.mappings().fields(), resource.name() );
        Path exportFile = csvFileWriter.writeExportFile( resource );

        return new ManifestEntry( resource.graphObjectType(), new CsvFiles( headerFile, exportFile ) );
    }
}
