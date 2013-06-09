package org.vento.sentiment.calculation

import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.impl.DefaultMessage
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.vento.sentiment.evaluation.VentoCalculationProcessor

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 24/12/12
 * Time: 21:28
 * To change this template use File | Settings | File Templates.
 */
public class VentoCalculationProcessorTest {

    VentoCalculationProcessor target;
    Exchange exchange;

    @Before
    public void setUp() throws Exception {
        target = new VentoCalculationProcessor();

        CamelContext context = new DefaultCamelContext();
        exchange = new DefaultExchange(context);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testVentoCalculation() throws Exception {
        DBObject twit1 = new BasicDBObject(['text':'this is horrible','score':'1.0'])
        DBObject twit2 = new BasicDBObject(['text':'I don\'t know','score':'2.0'])
        DBObject twit3 = new BasicDBObject(['text':'this is the best','score':'3.0'])

    }
}

