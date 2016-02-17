package org.neo4j.integration.neo4j.importcsv.fields;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class EndIdTest
{
    @Test
    public void shouldImplementEqualityForEndIdsWithoutIdSpace()
    {
        // given
        EndId endId1 = new EndId();
        EndId endId2 = new EndId();
        EndId endId3 = new EndId( null );
        EndId endId4 = new EndId( new IdSpace( "id-space" ) );

        // then
        assertEquals( endId1, endId2 );
        assertEquals( endId2, endId1 );
        assertEquals( endId2, endId3 );
        assertEquals( endId1, endId3 );
        assertNotEquals( endId1, endId4 );
    }

    @Test
    public void shouldImplementEqualityForEndIdsWithIdSpace()
    {
        // given
        EndId endId1 = new EndId( new IdSpace( "id-space" ) );
        EndId endId2 = new EndId( new IdSpace( "id-space" ) );

        // then
        assertEquals( endId1, endId2 );
        assertEquals( endId2, endId1 );
    }
}
