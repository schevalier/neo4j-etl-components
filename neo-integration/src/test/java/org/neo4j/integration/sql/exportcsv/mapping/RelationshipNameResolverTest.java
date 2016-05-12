package org.neo4j.integration.sql.exportcsv.mapping;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RelationshipNameResolverTest
{
    @Test
    public void shouldReturnTableNameAsResolvedName() throws Exception
    {
        RelationshipNameResolver resolver = new RelationshipNameResolver( false );
        String name = resolver.resolve( "Person", "addressId" );
        assertThat( name, is( "Person" ) );
    }

    @Test
    public void shouldReturnColumnNameAsResolvedName() throws Exception
    {
        RelationshipNameResolver resolver = new RelationshipNameResolver( true );
        String name = resolver.resolve( "Person", "addressId" );
        assertThat( name, is( "addressId" ) );
    }
}