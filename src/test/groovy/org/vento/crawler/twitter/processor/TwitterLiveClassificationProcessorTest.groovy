package org.vento.crawler.twitter.processor

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.impl.DefaultMessage
import org.junit.Before
import org.junit.Test
import twitter4j.Status
import twitter4j.internal.json.StatusJSONImpl

public class TwitterLiveClassificationProcessorTest {

    Exchange exchange;

    private TwitterLiveClassificationProcessor target

    @Before
    public void setUp() throws Exception {
        target = new TwitterLiveClassificationProcessor()

        CamelContext context = new DefaultCamelContext();
        exchange = new DefaultExchange(context);
    }

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

        target.process(exchange);

        def body = exchange.getOut().getBody()
        assert body != null

        println body
    }
}
