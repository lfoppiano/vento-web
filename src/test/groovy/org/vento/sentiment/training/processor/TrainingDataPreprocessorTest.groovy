package org.vento.sentiment.training.processor;

import com.mongodb.BasicDBObject
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.impl.DefaultMessage
import org.junit.Before
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 17/10/13
 * Time: 22:33
 * To change this template use File | Settings | File Templates.
 */
public class TrainingDataPreprocessorTest {

    TrainingDataPreprocessor target

    @Before
    public void setUp() {
        target = new TrainingDataPreprocessor();
    }

    @Test
    public void testProcess() throws Exception {

        Message message = new DefaultMessage()
        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        message.setBody(new BasicDBObject(['text':'this is horrible','score':'1.0']));
        exchange.setIn(message)


        target.process(exchange);

        println exchange.getIn().getBody()

    }
}
