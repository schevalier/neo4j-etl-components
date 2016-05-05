package org.neo4j.integration.provisioning;

import java.net.URI;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.Test;

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
    public static Resource<Neo4j> neo4j( Neo4jVersion version, Path directory )
    {
        return new LazyResource<>( new LazyResource.Lifecycle<Neo4j>()
        {
            @Override
            public Neo4j create() throws Exception
            {
                URI uri = ReleaseDownloads.uriFor( version, Neo4jEdition.Enterprise, PackageType.Tarball );
                Stream<Path> files = new DownloadableFile( uri ).downloadTo( directory ).extract();

                Neo4j neo4j = new Neo4j( files.findFirst().orElseThrow( IllegalStateException::new ) );
                neo4j.disableAuth();
                return neo4j;
            }

            @Override
            public void destroy( Neo4j neo4j ) throws Exception
            {
                neo4j.close();
            }
        } );
    }

    @Test
    public void should()
    {
         // given
        System.out.println(ReleaseDownloads.uriFor( Neo4jVersion.v3_0_0, Neo4jEdition.Enterprise, PackageType.Tarball ));

         // when

         // then
    }
}
