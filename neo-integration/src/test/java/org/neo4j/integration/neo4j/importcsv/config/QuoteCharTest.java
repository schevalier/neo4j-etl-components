package org.neo4j.integration.neo4j.importcsv.config;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QuoteCharTest
{
    @Test
    public void shouldDoubleUpExistingQuoteCharInSuppliedValue() throws IOException
    {
        // given
        QuoteChar quoteChar = QuoteChar.TICK_QUOTES;
        String value = "some" + quoteChar.value() + "text";

        // when
        String quotedValue = quoteChar.enquote( value );

        // then
        assertEquals( quoteChar.value() + "some" + quoteChar.value() + quoteChar.value() + "text" + quoteChar.value(),
                quotedValue );
    }

    @Test
    public void shouldEscapeExistingEscapeCharsInSuppliedValue() throws IOException
    {
        // given
        QuoteChar quoteChar = QuoteChar.TICK_QUOTES;

        String value = "some\\\\text\\";

        // when
        String quotedValue = quoteChar.enquote( value );

        // then
        assertEquals( quoteChar.value() + "some\\\\\\\\text\\\\" + quoteChar.value(), quotedValue );
    }
}
