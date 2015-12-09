package org.neo4j.io;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class StreamFilterTest
{
    @Test
    public void shouldOnlyIncludeLinesThatSatisfyFilter() throws IOException
    {
        // given
        @SuppressWarnings("unchecked")
        StreamEventHandler<String> handler = (StreamEventHandler<String>) mock( StreamEventHandler.class );

        StreamFilter<String> streamFilter = new StreamFilter<>( handler, s -> s.startsWith( "A" ) );

        // when
        streamFilter.onLine( "Apple" );
        streamFilter.onLine( "Banana" );
        streamFilter.onLine( "Airship" );

        // then
        verify( handler ).onLine( "Apple" );
        verify( handler ).onLine( "Airship" );
        verifyNoMoreInteractions( handler );
    }

    @Test
    public void shouldDelegateExceptionsToInnerHandler() throws IOException
    {
        // given
        IOException expectedException = new IOException( "Bad stream" );

        @SuppressWarnings("unchecked")
        StreamEventHandler<String> handler = (StreamEventHandler<String>) mock( StreamEventHandler.class );

        StreamFilter<String> streamFilter = new StreamFilter<>( handler, s -> s.startsWith( "A" ) );

        // when
        streamFilter.onException( expectedException );

        // then
        verify( handler ).onException( expectedException );
        verifyNoMoreInteractions( handler );
    }

    @Test
    public void shouldDelegateOnCompletedEventsToInnerHandler() throws IOException
    {
        // given
        @SuppressWarnings("unchecked")
        StreamEventHandler<String> handler = (StreamEventHandler<String>) mock( StreamEventHandler.class );

        StreamFilter<String> streamFilter = new StreamFilter<>( handler, s -> s.startsWith( "A" ) );

        // when
        streamFilter.onCompleted();

        // then
        verify( handler ).onCompleted();
        verifyNoMoreInteractions( handler );
    }

    @Test
    public void shouldDelegateToInnerHandlerForContents() throws IOException
    {
        // given
        @SuppressWarnings("unchecked")
        StreamEventHandler<String> handler = (StreamEventHandler<String>) mock( StreamEventHandler.class );

        StreamFilter<String> streamFilter = new StreamFilter<>( handler, s -> s.startsWith( "A" ) );

        // when
        streamFilter.awaitContents( 1, TimeUnit.SECONDS );

        // then
        verify( handler ).awaitContents( 1, TimeUnit.SECONDS );
        verifyNoMoreInteractions( handler );
    }
}
