package org.neo4j.etl.sql;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Credentials
{
    private final String username;
    private final String password;

    public Credentials( String username, String password )
    {
        this.username = username;
        this.password = password;
    }

    public String username()
    {
        return username;
    }

    public String password()
    {
        return password;
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
        return HashCodeBuilder.reflectionHashCode( this );
    }
}
