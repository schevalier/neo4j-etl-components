package org.neo4j.integration.neo4j;

import java.util.Optional;

import org.neo4j.integration.util.Partial;

public enum PackageType
{
    Zip( "-windows.zip" ),
    Tarball( "-unix.tar.gz" ),
    Deb( "_all.deb" )
            {
                @Override
                public String format( Neo4jEdition edition, Neo4jVersion version )
                {
                    return String.format( "%s_%s%s", format( edition ), format( version ), suffix );
                }

                @Override
                protected boolean matches( String input )
                {
                    return (input.startsWith( "-" ) || input.startsWith( "_" )) && input.endsWith( suffix );
                }

                @Override
                protected Optional<Neo4jPackage> doParse( String input )
                {
                    String remainder = input.substring( 1, input.length() - suffix.length() );
                    Partial<Neo4jEdition> edition = edition( remainder );
                    return Optional.of( new Neo4jPackage( version( edition.remainder ), edition.result, this ) );
                }

                private String format( Neo4jEdition edition )
                {
                    return edition == Neo4jEdition.Community ? "" : "-" + edition.format();
                }

                private String format( Neo4jVersion version )
                {
                    return version.toString( "." );
                }

                private Partial<Neo4jEdition> edition( String input )
                {
                    if ( !input.contains( "_" ) )
                    {
                        return new Partial<>( Neo4jEdition.Community, input );
                    }
                    else
                    {
                        return Neo4jEdition.parse( input, "_" );
                    }
                }

                private Neo4jVersion version( String input )
                {
                    return Neo4jVersion.from( input, "." );
                }
            },
    Directory( "" );

    protected final String suffix;

    PackageType( String suffix )
    {
        this.suffix = suffix;
    }

    public String format( Neo4jEdition edition, Neo4jVersion version )
    {
        return String.format( "-%s-%s%s", edition.format(), version, suffix );
    }

    private Optional<Neo4jPackage> maybeParse( String input )
    {
        if ( matches( input ) )
        {
            return doParse( input );
        }
        return Optional.empty();
    }

    protected Optional<Neo4jPackage> doParse( String input )
    {
        String remainder = input.substring( 1, input.length() - suffix.length() );
        Partial<Neo4jEdition> edition = Neo4jEdition.parse( remainder, "-" );
        Neo4jVersion version = Neo4jVersion.from( edition.remainder );
        return Optional.of( new Neo4jPackage( version, edition.result, this ) );
    }

    protected boolean matches( String input )
    {
        return input.startsWith( "-" ) && input.endsWith( suffix );
    }

    public static Neo4jPackage parse( String input )
    {
        return packageType( input );
    }

    private static Neo4jPackage packageType( String input )
    {
        for ( PackageType packageType : PackageType.values() )
        {
            Optional<Neo4jPackage> parsed = packageType.maybeParse( input );
            if ( parsed.isPresent() )
            {
                return parsed.get();
            }
        }
        throw new IllegalArgumentException( String.format( "'%s' is not a recognizable package", input ) );
    }
}
