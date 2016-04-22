package org.neo4j.integration.neo4j.importcsv.fields;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IdSpaceTest
{
    @Test
    public void shouldConvertIdSpaceNameToLowercase()
    {
        // given
        IdSpace idSpace = new IdSpace( "test.Person" );

        // then
        assertEquals( "test.person", idSpace.value() );
    }
}
