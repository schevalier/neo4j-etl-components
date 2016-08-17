package org.neo4j.etl.neo4j;

public class Neo4jDescriptor
{
    private final PackageType packageType;
    private final Neo4jEdition edition;
    private final Neo4jVersion version;

    public Neo4jDescriptor( PackageType packageType, Neo4jEdition edition, Neo4jVersion version )
    {
        this.packageType = packageType;
        this.edition = edition;
        this.version = version;
    }

    public PackageType packageType()
    {
        return packageType;
    }

    public Neo4jEdition edition()
    {
        return edition;
    }

    public Neo4jVersion version()
    {
        return version;
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

        Neo4jDescriptor that = (Neo4jDescriptor) o;

        return packageType == that.packageType && edition == that.edition && version.equals( that.version );
    }

    @Override
    public int hashCode()
    {
        int result = packageType.hashCode();
        result = 31 * result + edition.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }
}
