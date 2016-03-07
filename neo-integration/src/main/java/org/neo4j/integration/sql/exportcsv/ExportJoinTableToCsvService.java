package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.RelationshipConfig;
import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFilesWriter;
import org.neo4j.integration.sql.exportcsv.mapping.JoinTableToCsvFieldMapper;
import org.neo4j.integration.sql.metadata.JoinTable;

class ExportJoinTableToCsvService implements ExportDatabaseObjectToCsvService
{
    private final JoinTable joinTable;

    public ExportJoinTableToCsvService( JoinTable joinTable )
    {
        this.joinTable = joinTable;
    }

    @Override
    public GraphDataConfig exportToCsv( DatabaseExportSqlSupplier sqlSupplier,
                                        HeaderFileWriter headerFileWriter,
                                        CsvFileWriter csvFileWriter,
                                        ExportToCsvConfig config ) throws Exception
    {
        Collection<Path> files = new CsvFilesWriter<JoinTable>( headerFileWriter, csvFileWriter )
                .write( joinTable, new JoinTableToCsvFieldMapper( config.formatting() ), sqlSupplier );

        return importConfig -> importConfig.addRelationshipConfig( RelationshipConfig.builder()
                .addInputFiles( files )
                .build() );
    }
}
