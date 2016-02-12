package org.neo4j.integration.io;

import java.net.URI;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.integration.neo4j.Neo4jEdition;
import org.neo4j.integration.neo4j.Neo4jVersion;
import org.neo4j.integration.neo4j.PackageType;
import org.neo4j.integration.neo4j.ReleaseDownloads;
import org.neo4j.integration.util.ResourceRule;
import org.neo4j.integration.util.TemporaryDirectory;

import static org.junit.Assert.assertEquals;

public class DownloadableFileTest
{
    @Rule
    public final ResourceRule<Path> tempDirectory = new ResourceRule<>( TemporaryDirectory.temporaryDirectory() );

    @Test
    public void shouldDownloadNeo4j() throws Exception
    {
        URI uri = ReleaseDownloads.uriFor( Neo4jVersion.v2_1_3, Neo4jEdition.Enterprise, PackageType.Tarball );
        Stream<Path> files = new DownloadableFile( uri ).downloadTo( tempDirectory.get() ).extract();

        assertEquals( 1, files.count() );
    }
}
