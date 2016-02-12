package org.neo4j.integration.neo4j;

import java.net.URI;

import static java.lang.String.format;

public class ReleaseDownloads
{
    public static URI uriFor( Neo4jVersion version, Neo4jEdition edition, PackageType packageType )
    {
        return uriFor( new Neo4jPackage( version, edition, packageType ) );
    }

    public static URI uriFor( Neo4jPackage pkg )
    {
        if ( pkg.version.isSnapshot() )
        {
            throw new IllegalArgumentException( pkg.version + " is not a release version" );
        }

        return URI.create( format( "http://dist.neo4j.org/%s", pkg.filename() ) );
    }
}
