package org.neo4j.integration.sql.exportcsv.io;

import java.nio.file.Path;

import org.neo4j.integration.neo4j.importcsv.config.CsvFiles;
import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.exportcsv.DatabaseExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.exportcsv.mapping.DatabaseObjectToCsvFieldMapper;
import org.neo4j.integration.sql.metadata.DatabaseObject;

public class CsvFilesWriter<T extends DatabaseObject>
{
    private final HeaderFileWriter headerFileWriter;
    private final CsvFileWriter csvFileWriter;

    public CsvFilesWriter( HeaderFileWriter headerFileWriter, CsvFileWriter csvFileWriter )
    {
        this.headerFileWriter = headerFileWriter;
        this.csvFileWriter = csvFileWriter;
    }

    public CsvFiles write( T source,
                           DatabaseObjectToCsvFieldMapper<T> mapper,
                           DatabaseExportSqlSupplier sqlSupplier ) throws Exception
    {
        ColumnToCsvFieldMappings mappings = mapper.createMappings( source );

        Path headerFile = headerFileWriter.writeHeaderFile( mappings.fields(), source.descriptor() );
        Path exportFile = csvFileWriter.writeExportFile(
                mappings,
                sqlSupplier,
                source.descriptor(),
                mapper.writeRowWithNullsStrategy() );

        return new CsvFiles( headerFile, exportFile );
    }
}
