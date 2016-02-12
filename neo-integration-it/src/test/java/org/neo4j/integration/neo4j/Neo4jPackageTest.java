package org.neo4j.integration.neo4j;

import java.io.File;
import java.net.URI;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static java.lang.String.format;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import static org.neo4j.integration.neo4j.Neo4jEdition.Community;
import static org.neo4j.integration.neo4j.Neo4jEdition.Enterprise;
import static org.neo4j.integration.neo4j.Neo4jVersion.v1_9_0;
import static org.neo4j.integration.neo4j.Neo4jVersion.v1_9_7;
import static org.neo4j.integration.neo4j.Neo4jVersion.v2_1_2;
import static org.neo4j.integration.neo4j.Neo4jVersion.v2_1_4;
import static org.neo4j.integration.neo4j.Neo4jVersion.v2_2_0;
import static org.neo4j.integration.neo4j.Neo4jVersion.v3_0_0;
import static org.neo4j.integration.neo4j.PackageType.Deb;
import static org.neo4j.integration.neo4j.PackageType.Directory;
import static org.neo4j.integration.neo4j.PackageType.Tarball;
import static org.neo4j.integration.neo4j.PackageType.Zip;

@RunWith(Parameterized.class)
public class Neo4jPackageTest
{
    @Parameterized.Parameters(name = "{0}")
    public static Iterable<Object[]> data()
    {
        return Arrays.asList(
                new Object[]{"neo4j-enterprise-2.1.4-unix.tar.gz", Tarball, Enterprise, v2_1_4},
                new Object[]{"neo4j-enterprise-1.9-unix.tar.gz", Tarball, Enterprise, v1_9_0},
                new Object[]{"neo4j-community-1.9.7-SNAPSHOT-windows.zip", Zip, Community, v1_9_7.asSnapshotVersion()},
                new Object[]{"neo4j-enterprise-2.2.0", Directory, Enterprise, v2_2_0},
                new Object[]{"neo4j-enterprise-2.2.0-RC1-unix.tar.gz", Tarball, Enterprise, v2_2_0.prerelease( "RC1" )},
                new Object[]{"neo4j-enterprise_3.0.0_all.deb", Deb, Enterprise, v3_0_0},
                new Object[]{"neo4j-enterprise_3.0.0.M01_all.deb", Deb, Enterprise, v3_0_0.prerelease( "M01" )},
                new Object[]{"neo4j_2.1.2_all.deb", Deb, Community, v2_1_2}
        );
    }

    @Parameterized.Parameter(0)
    public String filename;

    @Parameterized.Parameter(1)
    public PackageType packageType;

    @Parameterized.Parameter(2)
    public Neo4jEdition edition;

    @Parameterized.Parameter(3)
    public Neo4jVersion version;

    @Test
    public void shouldParseFile()
    {
        Neo4jPackage pkg = Neo4jPackage.from( new File( "/some/path/or/other/" + filename ) );
        assertThat( pkg.packageType, is( packageType ) );
        assertThat( pkg.edition, is( edition ) );
        assertThat( pkg.version, is( version ) );
    }

    @Test
    public void shouldParseUri()
    {
        Neo4jPackage pkg = Neo4jPackage.from( uri( filename ) );
        assertThat( pkg.packageType, is( packageType ) );
        assertThat( pkg.edition, is( edition ) );
        assertThat( pkg.version, is( version ) );
    }

    @Test
    public void shouldRoundTripFilename()
    {
        Neo4jPackage pkg = Neo4jPackage.from( uri( filename ) );
        assertThat( pkg.filename(), is( filename ) );
    }

    public static URI uri( String filename )
    {
        return URI.create( format( "http://foo.example.com/%s", filename ) );
    }
}
