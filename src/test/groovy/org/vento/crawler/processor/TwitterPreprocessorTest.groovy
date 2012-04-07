package org.vento.crawler.processor;


import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.impl.DefaultMessage
import org.junit.Before
import org.junit.Test

public class TwitterPreprocessorTest {

    Exchange exchange;

    @Before
    public void setUp() throws Exception {

        CamelContext context = new DefaultCamelContext();
        exchange = new DefaultExchange(context);

        String body = getClass().getResourceAsStream('twitter-response-body.json').getText();
        Message messageIn = new DefaultMessage();
        messageIn.setBody(body)
        exchange.setIn(messageIn)
    }

    @Test
    public void testProcess() throws Exception {
        TwitterPreprocessor target = new TwitterPreprocessor();
        target.process(exchange);

        def body = exchange.getOut().getBody()
        assert body != null

        assert body.twits.size() == 15
        assert body.twits.get(0).id == '123505817893863425'
        assert body.twits.get(14).text == 'My better is better than your better #Nike'
    }
}
