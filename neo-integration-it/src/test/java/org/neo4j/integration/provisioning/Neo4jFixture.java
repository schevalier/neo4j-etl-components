package org.neo4j.integration.provisioning;

import java.net.URI;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.neo4j.integration.io.DownloadableFile;
import org.neo4j.integration.neo4j.Neo4j;
import org.neo4j.integration.neo4j.Neo4jEdition;
import org.neo4j.integration.neo4j.Neo4jVersion;
import org.neo4j.integration.neo4j.PackageType;
import org.neo4j.integration.neo4j.ReleaseDownloads;
import org.neo4j.integration.util.LazyResource;
import org.neo4j.integration.util.Resource;

public class Neo4jFixture
{
    public static Resource<Neo4j> neo4j( Path directory, Neo4jVersion version )
    {
        return new LazyResource<>( new LazyResource.Lifecycle<Neo4j>()
        {
            @Override
            public Neo4j create() throws Exception
            {
                URI uri = ReleaseDownloads.uriFor( version, Neo4jEdition.Enterprise, PackageType.Tarball );
                Stream<Path> files = new DownloadableFile( uri ).downloadTo( directory ).extract();

                return new Neo4j( files.findFirst().orElseThrow( IllegalStateException::new ) );
            }

            @Override
            public void destroy( Neo4j neo4j ) throws Exception
            {
                neo4j.close();
            }
        } );
    }
}
