package org.neo4j.integration.mysql.exportcsvnew;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.neo4j.integration.cli.Commands;
import org.neo4j.integration.mysql.SqlRunner;
import org.neo4j.integration.mysql.exportcsvnew.config.ColumnToCsvFieldMappings;
import org.neo4j.integration.mysql.exportcsvnew.config.ExportToCsvConfig;
import org.neo4j.integration.mysql.exportcsvnew.config.JoinMapper;
import org.neo4j.integration.mysql.exportcsvnew.config.TableMapper;
import org.neo4j.integration.mysql.exportcsvnew.metadata.Join;
import org.neo4j.integration.mysql.exportcsvnew.metadata.Table;
import org.neo4j.integration.neo4j.importcsv.HeaderFile;
import org.neo4j.integration.neo4j.importcsv.config.Delimiter;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfig;
import org.neo4j.integration.neo4j.importcsv.config.GraphDataConfigSupplier;
import org.neo4j.integration.neo4j.importcsv.config.NodeConfig;
import org.neo4j.integration.neo4j.importcsv.config.QuoteChar;
import org.neo4j.integration.neo4j.importcsv.config.RelationshipConfig;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class ExportCommand
{
    private final ExportToCsvConfig config;

    public ExportCommand( ExportToCsvConfig config )
    {
        this.config = config;
    }

    public GraphDataConfig execute() throws Exception
    {
        QuoteChar quote = config.formatting().quoteCharacter();
        Delimiter delimiter = config.formatting().delimiter();

        if ( Files.notExists( config.destination() ) )
        {
            Files.createDirectories( config.destination() );
        }

        Commands.commands( "chmod", "0777", config.destination().toString() ).execute().await();

        Collection<GraphDataConfigSupplier> graphDataConfigSuppliers = new ArrayList<>();


        try ( SqlRunner sqlRunner = new SqlRunner( config.connectionConfig() ) )
        {
            for ( Table table : config.tables() )
            {
                String exportId = UUID.randomUUID().toString();

                ColumnToCsvFieldMappings mappings = new TableMapper().createExportCsvConfigFor( table, quote );

                Path headerFile = new HeaderFile( config.destination(), config.formatting() )
                        .createHeaderFile( mappings.fields(), exportId );
                Path exportFile = config.destination().resolve( format( "%s.csv", exportId ) );

                sqlRunner.execute( mappings.sql( exportFile, delimiter ) ).await();

                graphDataConfigSuppliers.add( NodeConfig.builder()
                        .addInputFiles( asList( headerFile, exportFile ) )
                        .addLabel( table.name().simpleName() )
                        .build() );

            }

            for ( Join join : config.joins() )
            {
                String exportId = UUID.randomUUID().toString();

                ColumnToCsvFieldMappings mappings = new JoinMapper().createExportCsvConfigFor( join, quote );

                Path headerFile = new HeaderFile( config.destination(), config.formatting() )
                        .createHeaderFile( mappings.fields(), exportId );
                Path exportFile = config.destination().resolve( format( "%s.csv", exportId ) );

                sqlRunner.execute( mappings.sql( exportFile, delimiter ) ).await();

                graphDataConfigSuppliers.add( RelationshipConfig.builder()
                        .addInputFiles( asList( headerFile, exportFile ) )
                        .build() );

            }
        }


        return new GraphDataConfig( graphDataConfigSuppliers );
    }
}
