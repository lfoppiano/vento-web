package org.vento.crawler.amazon.processor

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.impl.DefaultMessage
import org.apache.commons.io.IOUtils
import org.junit.Before
import org.junit.Test
import org.vento.crawler.amazon.processor.AmazonReviewPageCalculatorProcessor

/**
 * Created by IntelliJ IDEA.
 * User: lfoppiano
 * Date: 3/17/12
 * Time: 8:40 PM
 * To change this template use File | Settings | File Templates.
 */
class AmazonReviewPageCalculatorProcessorTest {
    
    InputStream inputStream
    InputStream inputStreamNoMax

    @Before
    public void setUp() throws Exception {

        inputStream = this.getClass().getResourceAsStream("example.response.amazon.review.html")
        inputStreamNoMax = this.getClass().getResourceAsStream("example.response.amazon.review.noMaxPage.html")
    }

    @Test
    public void testProcessWithMax() throws Exception {
        CamelContext context = new DefaultCamelContext()
        Exchange exchange = new DefaultExchange(context)

        Message inMessage = new DefaultMessage()

        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");
        String inputResponse = writer.toString();

        inMessage.setBody(inputResponse)
        exchange.setIn(inMessage)

        AmazonReviewPageCalculatorProcessor target = new AmazonReviewPageCalculatorProcessor()
        
        target.process(exchange)

        def output = exchange.getIn()

        assert output != null

        assert output.getHeaders() != null
        assert output.getBody() != null

        assert output.getHeader("maxPageNumber") == '75'

    }


    @Test
    public void testProcessWithNoMax() throws Exception {
        CamelContext context = new DefaultCamelContext()
        Exchange exchange = new DefaultExchange(context)

        Message inMessage = new DefaultMessage()

        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStreamNoMax, writer, "UTF-8");
        String inputResponse = writer.toString();

        inMessage.setBody(inputResponse)
        exchange.setIn(inMessage)

        AmazonReviewPageCalculatorProcessor target = new AmazonReviewPageCalculatorProcessor()

        target.process(exchange)

        def output = exchange.getIn()

        assert output != null

        assert output.getHeaders() != null
        assert output.getBody() != null

        assert output.getHeader("maxPageNumber") == '10'

    }
}
