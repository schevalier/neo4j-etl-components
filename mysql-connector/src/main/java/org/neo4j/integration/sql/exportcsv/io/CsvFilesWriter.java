package org.neo4j.integration.sql.exportcsv.io;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.sql.exportcsv.ExportSqlSupplier;
import org.neo4j.integration.sql.exportcsv.mapping.ColumnToCsvFieldMappings;
import org.neo4j.integration.sql.exportcsv.mapping.Mapper;
import org.neo4j.integration.sql.metadata.DatabaseObject;
import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;

import static java.util.Arrays.asList;

public class CsvFilesWriter<T extends DatabaseObject>
{
    private final HeaderFileWriter headerFileWriter;
    private final CsvFileWriter csvFileWriter;

    public CsvFilesWriter( HeaderFileWriter headerFileWriter, CsvFileWriter csvFileWriter )
    {
        this.headerFileWriter = headerFileWriter;
        this.csvFileWriter = csvFileWriter;
    }

    public Collection<Path> write( T source,
                                   Mapper<T> mapper,
                                   ExportSqlSupplier sqlSupplier ) throws Exception
    {
        ColumnToCsvFieldMappings mappings = mapper.createMappings( source );

        Path headerFile = headerFileWriter.writeHeaderFile( mappings.fields(), source.descriptor() );
        Path exportFile = csvFileWriter.writeExportFile( mappings, sqlSupplier, source.descriptor() );

        return asList( headerFile, exportFile );
    }
}
