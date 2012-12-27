package org.vento.crawler.amazon.processor;


import org.vento.model.Twits

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.impl.DefaultMessage
import org.apache.commons.io.IOUtils
import org.junit.Before
import org.junit.Test
import org.vento.crawler.amazon.processor.AmazonReviewProcessor
import org.vento.model.Twit

/**
 * Created by IntelliJ IDEA.
 * User: lfoppiano
 * Date: 3/16/12
 * Time: 8:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class AmazonReviewProcessorTest {
    
    InputStream inputStream

    @Before
    public void setUp() throws Exception {
        inputStream = this.getClass().getResourceAsStream("example.response.amazon.review.html")
    }

    @Test
    public void testProcess() throws Exception {
        CamelContext context = new DefaultCamelContext()
        Exchange exchange = new DefaultExchange(context)

        Message inMessage = new DefaultMessage()

        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");
        String inputResponse = writer.toString();

        inMessage.setBody(inputResponse)
        exchange.setIn(inMessage)

        AmazonReviewProcessor target = new AmazonReviewProcessor();
        target.process(exchange)

        Twits output = exchange.getOut().getBody()

        assert output != null

        //they would be 10 but one of them is skipped because the sentence 'This review is from:' is missing
        assert output.twits.size() == 9

        def sum = 0
        assert output.twits.each{ Twit twit ->
            sum += twit.getText().count('<br />')
            println twit.getScore()
        }

        assert sum == 0
    }
    
}