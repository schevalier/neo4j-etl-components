package org.neo4j.integration.neo4j;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;

import static java.lang.String.format;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Neo4jVersionTest
{
    @Test
    public void shouldIdentifySeries()
    {
        assertTrue( Neo4jVersion.v1_9_5.belongsTo( Neo4jSeries.v1_9_x ) );
        assertFalse( Neo4jVersion.v1_9_5.belongsTo( Neo4jSeries.v2_x_x ) );

        assertFalse( Neo4jVersion.v1_9_5.belongsTo( Neo4jSeries.v2_x_x ) );
        assertTrue( Neo4jVersion.v2_1_4.belongsTo( Neo4jSeries.v2_x_x ) );
    }

    @Test
    public void shouldTreatVersionWithoutPatchNumberAsThoughItWereZero()
    {
        assertEquals( Neo4jVersion.v2_1_0, Neo4jVersion.from( "2.1" ) );
    }

    @Test
    public void shouldParseStrings()
    {
        assertEquals( Neo4jVersion.v2_1_3, Neo4jVersion.from( "2.1.3" ) );
    }

    @Test
    public void shouldTakeAccountOfPreReleaseLabels()
    {
        assertThat( Neo4jVersion.from( "2.0.1-SNAPSHOT" ), is( not( Neo4jVersion.v2_0_1 ) ) );
        assertThat( Neo4jVersion.from( "2.0.1-SNAPSHOT" ), belongsTo( Neo4jSeries.v2_0_x ) );
    }

    @Test
    public void shouldRoundTripVersionWithPreReleaseLabel()
    {
        assertThat( Neo4jVersion.from( "2.2.0-newt" ).toString(), is( "2.2.0-newt" ) );
    }

    @Test
    public void shouldDealWithMissingPatchReleaseNumber()
    {
        assertEquals( Neo4jVersion.v2_0_0, Neo4jVersion.from( "2.0" ) );
        assertEquals( Neo4jVersion.v2_2_0.prerelease( "SNAPSHOT" ), Neo4jVersion.from( "2.2-SNAPSHOT" ) );
    }

    @Test
    public void shouldDeniesTheExistenceOfVersionsBefore_v1_9_0()
    {
        try
        {
            Neo4jVersion.from( "1.8.3" );
            fail( "Should not allow versions before 1.9.0 because the library's behaviour is not specified." );
        }
        catch ( IllegalArgumentException ignored )
        {

        }
    }

    @Test
    public void shouldIncludeZeroPatchNumberFor_v2_0_0_andLater()
    {
        assertThat( Neo4jVersion.v2_0_0.toString(), is( "2.0.0" ) );
        assertThat( Neo4jVersion.v2_1_0.toString(), is( "2.1.0" ) );
        assertThat( Neo4jVersion.v2_2_0.toString(), is( "2.2.0" ) );
    }

    @Test
    public void shouldNotIncludeZeroPatchNumberFor_v1_9_0_becauseThatWasTheStandardBefore_v2_0_0()
    {
        assertThat( Neo4jVersion.v1_9_0.toString(), is( "1.9" ) );
        assertThat( Neo4jVersion.v1_9_1.toString(), is( "1.9.1" ) );
    }

    @Test
    public void shouldNotIncludeZeroPatchNumberForSnapshots()
    {
        assertThat( Neo4jVersion.v2_2_0.prerelease( "SNAPSHOT" ).toString(), is( "2.2-SNAPSHOT" ) );
    }

    @Test
    public void shouldBeAbleToCreateAPreReleaseVersion()
    {
        assertThat( Neo4jVersion.v2_2_0.prerelease( "SNAPSHOT" ), is( Neo4jVersion.from( "2.2.0-SNAPSHOT" ) ) );
    }

    @Test
    public void shouldNotBeAbleToCreateAPreReleaseVersionOfAPreReleaseVersion()
    {
        try
        {
            Neo4jVersion.from( "2.2.0-SNAPSHOT" ).prerelease( "M09" );
            fail( "Expected IllegalStateException" );
        }
        catch ( IllegalStateException ignored )
        {
        }
    }

    @Test
    public void shouldOrderMatchingReleasesCorrectly()
    {
        assertThat( Neo4jVersion.v2_2_0, not( isAfter( Neo4jVersion.v2_2_0 ) ) );
        assertThat( Neo4jVersion.v2_2_0, not( isBefore( Neo4jVersion.v2_2_0 ) ) );
    }

    @Test
    public void shouldOrderPreReleaseAreBeforeReleases()
    {
        assertThat( Neo4jVersion.v2_2_0.prerelease( "M01" ), isBefore( Neo4jVersion.v2_2_0 ) );
        assertThat( Neo4jVersion.v2_2_0.prerelease( "M01" ), not( isAfter( Neo4jVersion.v2_2_0 ) ) );

        assertThat( Neo4jVersion.v2_2_0, isAfter( Neo4jVersion.v2_2_0.prerelease( "M01" ) ) );
        assertThat( Neo4jVersion.v2_2_0, not( isBefore( Neo4jVersion.v2_2_0.prerelease( "M01" ) ) ) );
    }

    @Test
    public void shouldOrderPreReleasesAlphanumerically()
    {
        assertThat( Neo4jVersion.v2_2_0.prerelease( "1" ), isBefore( Neo4jVersion.v2_2_0.prerelease( "2" ) ) );
        assertThat( Neo4jVersion.v2_2_0.prerelease( "1" ), not( isAfter( Neo4jVersion.v2_2_0.prerelease( "2" ) ) ) );

        assertThat( Neo4jVersion.v2_2_0.prerelease( "a" ), isBefore( Neo4jVersion.v2_2_0.prerelease( "b" ) ) );
        assertThat( Neo4jVersion.v2_2_0.prerelease( "a" ), not( isAfter( Neo4jVersion.v2_2_0.prerelease( "b" ) ) ) );

        assertThat( Neo4jVersion.v2_2_0.prerelease( "a1" ), isBefore( Neo4jVersion.v2_2_0.prerelease( "a2" ) ) );
        assertThat( Neo4jVersion.v2_2_0.prerelease( "a1" ), not( isAfter( Neo4jVersion.v2_2_0.prerelease( "a2" ) ) ) );

        assertThat( Neo4jVersion.v2_2_0.prerelease( "M01" ), isBefore( Neo4jVersion.v2_2_0.prerelease( "M02" ) ) );
        assertThat( Neo4jVersion.v2_2_0.prerelease( "M01" ), not( isAfter( Neo4jVersion.v2_2_0.prerelease( "M02" ) )
        ) );

        assertThat( Neo4jVersion.v2_2_0.prerelease( "M99" ), isBefore( Neo4jVersion.v2_2_0.prerelease( "RC1" ) ) );
        assertThat( Neo4jVersion.v2_2_0.prerelease( "M99" ), not( isAfter( Neo4jVersion.v2_2_0.prerelease( "RC1" ) )
        ) );
    }

    @Test
    public void shouldOrderSnapshotsAfterOtherPreReleasesBecauseTheyAreTheCuttingEdge()
    {
        assertThat( Neo4jVersion.v2_2_0.prerelease( "SNAPSHOT" ), isAfter( Neo4jVersion.v2_2_0.prerelease( "M01" ) ) );
        assertThat( Neo4jVersion.v2_2_0.prerelease( "SNAPSHOT" ),
                not( isBefore( Neo4jVersion.v2_2_0.prerelease( "M01" ) ) ) );
    }

    @Test
    public void shouldDifferentiateBetweenMilestoneReleases()
    {
        // given
        Neo4jVersion version1 = Neo4jVersion.from( "2.2.0-M01" );
        Neo4jVersion version2 = Neo4jVersion.from( "2.2.0-M02" );
        Neo4jVersion version3 = Neo4jVersion.from( "2.2.0-M03" );

        // then
        assertTrue( version1.belongsTo( Neo4jSeries.v2_2_x ) );
        assertTrue( version2.belongsTo( Neo4jSeries.v2_2_x ) );
        assertTrue( version3.belongsTo( Neo4jSeries.v2_2_x ) );

        assertNotEquals( version1, version2 );
        assertNotEquals( version1, version3 );
        assertNotEquals( version2, version3 );

        assertEquals( version1, Neo4jVersion.from( "2.2.0-M01" ) );
    }


    @Test
    public void shouldCorrectlyVersion3Strings() throws Throwable
    {
        // Given
        Neo4jVersion version3 = Neo4jVersion.from( "3.0.0-SNAPSHOT" );
        Neo4jVersion versionNoSnapshot = Neo4jVersion.from( "3.0.0" );

        // Then
        assertEquals( "3.0.0-SNAPSHOT", version3.toString() );
        assertEquals( "3.0.0", versionNoSnapshot.toString() );
    }

    private Matcher<Neo4jVersion> isBefore( final Neo4jVersion version )
    {
        return new CustomTypeSafeMatcher<Neo4jVersion>( format( "comes before %s", version ) )
        {
            @Override
            protected boolean matchesSafely( Neo4jVersion item )
            {
                return item.isBefore( version );
            }
        };
    }

    private Matcher<Neo4jVersion> isAfter( final Neo4jVersion version )
    {
        return new CustomTypeSafeMatcher<Neo4jVersion>( format( "comes after %s", version ) )
        {
            @Override
            protected boolean matchesSafely( Neo4jVersion item )
            {
                return item.isAfter( version );
            }
        };
    }

    private Matcher<? super Neo4jVersion> belongsTo( final Neo4jSeries series )
    {
        return new CustomTypeSafeMatcher<Neo4jVersion>( format( "a member of %s", series ) )
        {
            @Override
            protected boolean matchesSafely( Neo4jVersion version )
            {
                return version.belongsTo( series );
            }
        };
    }
}
