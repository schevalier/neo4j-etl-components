package org.neo4j.integration.neo4j;

import java.io.File;
import java.net.URI;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.neo4j.integration.util.FileUtils;

public class Neo4jPackage
{
    private static final String prefix = "neo4j";

    public static Neo4jPackage from( URI packageUri )
    {
        String filename = FileUtils.filenameFromUri( packageUri );

        if ( !filename.startsWith( prefix ) )
        {
            throw new IllegalArgumentException( "Not a recognizable package: " + filename );
        }

        String remainder = filename.substring( prefix.length() );
        return PackageType.parse( remainder );
    }

    public static Neo4jPackage from( File file )
    {
        return from( file.toURI() );
    }

    public final Neo4jEdition edition;
    public final Neo4jVersion version;
    public final PackageType packageType;

    public Neo4jPackage( Neo4jVersion version, Neo4jEdition edition, PackageType packageType )
    {
        this.version = version;
        this.edition = edition;
        this.packageType = packageType;
    }

    public String filename()
    {
        return prefix + packageType.format( edition, version );
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals( Object o )
    {
        return EqualsBuilder.reflectionEquals( this, o );
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode( 31 );
    }

    @Override
    public String toString()
    {
        return String.format( "Neo4j[%s, %s, %s]", version, edition, packageType );
    }

}
