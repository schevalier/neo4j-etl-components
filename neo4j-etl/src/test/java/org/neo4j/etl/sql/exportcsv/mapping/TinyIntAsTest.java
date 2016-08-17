package org.neo4j.etl.sql.exportcsv.mapping;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TinyIntAsTest
{
    @Test
    public void parseTinyIntAsOrReturnByteByDefault() throws Exception
    {
        assertThat( TinyIntAs.BOOLEAN, is( TinyIntAs.parse( "boolean" ) ) );
        assertThat( TinyIntAs.BYTE, is( TinyIntAs.parse( "byte" ) ) );
        assertThat( TinyIntAs.BYTE, is( TinyIntAs.parse( "banana" ) ) );
    }
}
