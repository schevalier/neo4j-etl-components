package org.neo4j.integration.neo4j;

import java.io.File;
import java.net.URI;

public class Neo4jPackage
{
    private static final String prefix = "neo4j";

    public static Neo4jPackage from( URI packageUri )
    {
        String filename = new File( packageUri.getPath() ).getName();

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

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        Neo4jPackage that = (Neo4jPackage) o;

        if ( edition != that.edition )
        {
            return false;
        }
        if ( packageType != that.packageType )
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( version != null ? !version.equals( that.version ) : that.version != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = edition != null ? edition.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (packageType != null ? packageType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return String.format( "Neo4j[%s, %s, %s]", version, edition, packageType );
    }

}
