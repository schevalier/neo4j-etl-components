package org.neo4j.integration.neo4j.importcsv.fields;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class IdTest
{
    @Test
    public void shouldImplementEqualityForIdsWithoutNameOrIdSpace()
    {
        // given
        Id id1 = new Id();
        Id id2 = new Id();
        Id id3 = new Id( (String) null );
        Id id4 = new Id( (IdSpace) null );
        Id id5 = new Id( "id" );
        Id id6 = new Id( new IdSpace( "id-space" ) );
        Id id7 = new Id( "id", new IdSpace( "id-space" ) );
        Id id8 = new Id( null, null );

        // then
        assertEquals( id1, id2 );
        assertEquals( id2, id1 );
        assertEquals( id1, id3 );
        assertEquals( id1, id4 );
        assertEquals( id1, id8 );

        assertNotEquals( id1, id5 );
        assertNotEquals( id1, id6 );
        assertNotEquals( id1, id7 );
    }

    @Test
    public void shouldImplementEqualityForIdsWithName()
    {
        // given
        Id id1 = new Id("id");
        Id id2 = new Id("id");
        Id id3 = new Id("id", null );
        Id id4 = new Id( "not-id" );
        Id id5 = new Id( new IdSpace( "id-space" ) );
        Id id6 = new Id( "id", new IdSpace( "id-space" ) );

        // then
        assertEquals( id1, id2 );
        assertEquals( id2, id1 );
        assertEquals( id1, id3 );

        assertNotEquals( id1, id4 );
        assertNotEquals( id1, id5 );
        assertNotEquals( id1, id6 );
    }

    @Test
    public void shouldImplementEqualityForIdsWithIdSpace()
    {
        // given
        Id id1 = new Id("id", new IdSpace( "id-space" ));
        Id id2 = new Id("id", new IdSpace( "id-space" ));
        Id id3 = new Id("id");
        Id id4 = new Id( new IdSpace( "id-space" ) );
        Id id5 = new Id( "not-id", new IdSpace( "id-space" ) );
        Id id6 = new Id( "id", new IdSpace( "not-id-space" ) );

        // then
        assertEquals( id1, id2 );
        assertEquals( id2, id1 );

        assertNotEquals( id1, id3 );
        assertNotEquals( id1, id4 );
        assertNotEquals( id1, id5 );
        assertNotEquals( id1, id6 );
    }
}
