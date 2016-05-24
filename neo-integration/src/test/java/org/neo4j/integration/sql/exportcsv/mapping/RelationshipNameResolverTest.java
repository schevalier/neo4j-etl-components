package org.neo4j.integration.sql.exportcsv.mapping;

import org.junit.Test;

import org.neo4j.integration.RelationshipNameFrom;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RelationshipNameResolverTest
{
    @Test
    public void shouldReturnTableNameAsResolvedName() throws Exception
    {
        RelationshipNameResolver resolver = new RelationshipNameResolver( RelationshipNameFrom.TABLE_NAME );
        String name = resolver.resolve( "Person", "addressId" );
        assertThat( name, is( "Person" ) );
    }

    @Test
    public void shouldReturnColumnNameAsResolvedName() throws Exception
    {
        RelationshipNameResolver resolver = new RelationshipNameResolver( RelationshipNameFrom.COLUMN_NAME );
        String name = resolver.resolve( "Person", "addressId" );
        assertThat( name, is( "addressId" ) );
    }
}
