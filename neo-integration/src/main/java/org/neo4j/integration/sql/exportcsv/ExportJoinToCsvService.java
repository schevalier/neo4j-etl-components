package org.neo4j.integration.sql.exportcsv;

import java.nio.file.Path;
import java.util.Collection;

import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.RelationshipConfig;
import org.neo4j.integration.neo4j.importcsv.io.HeaderFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFileWriter;
import org.neo4j.integration.sql.exportcsv.io.CsvFilesWriter;
import org.neo4j.integration.sql.exportcsv.mapping.JoinToCsvFieldMapper;
import org.neo4j.integration.sql.metadata.Join;

class ExportJoinToCsvService implements ExportDatabaseObjectToCsvService
{
    private final Join join;

    public ExportJoinToCsvService( Join join )
    {
        this.join = join;
    }

    @Override
    public GraphDataConfig exportToCsv( DatabaseExportSqlSupplier sqlSupplier,
                                        HeaderFileWriter headerFileWriter,
                                        CsvFileWriter csvFileWriter,
                                        ExportToCsvConfig config ) throws Exception
    {
        Collection<Path> files = new CsvFilesWriter<Join>( headerFileWriter, csvFileWriter )
                .write( join, new JoinToCsvFieldMapper( config.formatting() ), sqlSupplier );

        return importConfig -> importConfig.addRelationshipConfig( RelationshipConfig.builder()
                .addInputFiles( files )
                .build() );
    }
}
