package org.vento.crawler.twitter.processor

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.impl.DefaultMessage
import org.easymock.EasyMock
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.vento.sentiment.classification.ClassificationWrapper
import twitter4j.Status
import twitter4j.internal.json.StatusJSONImpl

import static org.easymock.EasyMock.*
import static org.easymock.EasyMock.createMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.objectweb.asm.util.CheckClassAdapter.verify

public class TwitterLiveClassificationProcessorTest {

    Exchange exchange;

    private TwitterLiveClassificationProcessor target

    private ClassificationWrapper mockClassificationWrapper

    @Before
    public void setUp() throws Exception {
        mockClassificationWrapper = createMock(ClassificationWrapper.class)

        target = new TwitterLiveClassificationProcessor()
        target.setClassificationWrapper(mockClassificationWrapper)

        CamelContext context = new DefaultCamelContext();
        exchange = new DefaultExchange(context);
    }

    @Ignore("Too much work to make easymock run under groovy, to be either rewritten in java or moved to gmock")
    @Test
    public void testProcessEnd2End() throws Exception {

        List<Status> bodyIn = new ArrayList<Status>();
        bodyIn.add(new StatusJSONImpl(
                id: 12345,
                text:  "ciao a tutti :)"
        ))
        bodyIn.add(new StatusJSONImpl(
                id: 133,
                text:  "triste :("
        ))

        Message messageIn = new DefaultMessage();
        messageIn.setBody(bodyIn)
        exchange.setIn(messageIn)

        expect(mockClassificationWrapper.classify(null)).andReturn("3.0");

        replay(mockClassificationWrapper)

        target.process(exchange);

        verify(mockClassificationWrapper)

        def body = exchange.getOut().getBody()
        assert body != null

        println body
    }
}
