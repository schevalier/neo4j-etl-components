package org.neo4j.etl.neo4j.importcsv.fields;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class StartIdTest
{
    @Test
    public void shouldImplementEqualityForEndIdsWithoutIdSpace()
    {
        // given
        StartId startId1 = new StartId();
        StartId startId2 = new StartId();
        StartId startId3 = new StartId( null );
        StartId startId4 = new StartId( new IdSpace( "id-space" ) );

        // then
        assertEquals( startId1, startId2 );
        assertEquals( startId2, startId1 );
        assertEquals( startId2, startId3 );
        assertEquals( startId1, startId3 );
        assertNotEquals( startId1, startId4 );
    }

    @Test
    public void shouldImplementEqualityForEndIdsWithIdSpace()
    {
        // given
        StartId startId1 = new StartId( new IdSpace( "id-space" ) );
        StartId startId2 = new StartId( new IdSpace( "id-space" ) );

        // then
        assertEquals( startId1, startId2 );
        assertEquals( startId2, startId1 );
    }
}
