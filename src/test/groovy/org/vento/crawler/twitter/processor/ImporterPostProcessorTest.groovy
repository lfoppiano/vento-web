package org.vento.crawler.twitter.processor

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.impl.DefaultMessage
import org.junit.Before
import org.junit.Test
import org.vento.model.Twit
import org.vento.utility.VentoTypes

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 02/05/13
 * Time: 15:01
 * To change this template use File | Settings | File Templates.
 */
class ImporterPostProcessorTest {
    Exchange exchange;

    private ImporterPostProcessor target

    @Before
    public void setUp() throws Exception {
        target = new ImporterPostProcessor()

        CamelContext context = new DefaultCamelContext();
        exchange = new DefaultExchange(context);
    }

    @Test
    public void testProcessEnd2End() throws Exception {
        List<String> bodyIn = new ArrayList<String>()
        List<String> csv = new ArrayList<String>()
        bodyIn.add(csv)

        csv.add("1")
        csv.add("text myself")

        Message messageIn = new DefaultMessage();
        messageIn.setBody(bodyIn)
        exchange.setIn(messageIn)

        target.process(exchange);

        def body = exchange.getOut().getBody()
        assert body != null

        assert ((Twit) body).getText() == 'text myself'
        assert ((Twit) body).getScore() == '3.0'
        assert ((Twit) body).getType() == VentoTypes.TRAINING

    }
}
