package org.vento.crawler.twitter.processor;


import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.impl.DefaultMessage
import org.junit.Before
import org.junit.Test
import org.vento.model.Twit

public class TwitterPreprocessorTest {

    Exchange exchange;

    private TwitterPreprocessor target

    @Before
    public void setUp() throws Exception {
        target = new TwitterPreprocessor()

        CamelContext context = new DefaultCamelContext();
        exchange = new DefaultExchange(context);
    }

    @Test
    public void testProcessEnd2End() throws Exception {
        String bodyIn = getClass().getResourceAsStream('twitter-response-body.json').getText();
        Message messageIn = new DefaultMessage();
        messageIn.setBody(bodyIn)
        exchange.setIn(messageIn)

        target.process(exchange);

        def body = exchange.getOut().getBody()
        assert body != null

        assert body.twits.size() == 15
        assert body.twits.get(0).twitterId == '123505817893863425'
        assert body.twits.get(14).text == '_USER_ My better is better than your better _TAG_ _LINK_'
    }
}
