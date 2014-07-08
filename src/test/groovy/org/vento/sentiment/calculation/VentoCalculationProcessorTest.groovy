package org.vento.sentiment.calculation

import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import gate.Controller
import gate.Gate
import gate.util.DocumentProcessor
import gate.util.persistence.PersistenceManager
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.impl.DefaultMessage
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.vento.sentiment.classification.ClassificationWrapper
import org.vento.sentiment.evaluation.VentoCalculationProcessor

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 24/12/12
 * Time: 21:28
 * To change this template use File | Settings | File Templates.
 */
public class VentoCalculationProcessorTest {

    VentoCalculationProcessor target
    Exchange exchange
    DocumentProcessor gateAppliction

    @Before
    public void setUp() throws Exception {
        List<DBObject> twits = []
        Message twitsArrayMessage = new DefaultMessage()
        CamelContext context = new DefaultCamelContext();


        target = new VentoCalculationProcessor()
        def tempWrapper = new ClassificationWrapper()

        tempWrapper.setClassifier(gateAppliction)
        target.setClassificationWrapper(tempWrapper)
        twits << new BasicDBObject(['text':'this is horrible','score':'1.0'])
        twits << new BasicDBObject(['text':'I don\'t know','score':'2.0'])
        twits << new BasicDBObject(['text':'this is the best','score':'3.0'])
        twits << new BasicDBObject(['text':'I\'m so sad','score':'1.0'])
        twits << new BasicDBObject(['text':'I\'mso happy','score':'3.0'])
        twitsArrayMessage.setBody(twits)
        exchange = new DefaultExchange(context);
        exchange.setIn(twitsArrayMessage)
    }

    @After
    public void tearDown() throws Exception {

    }

    @Ignore
    @Test
    public void testVentoCalculation() throws Exception {
        target.process(exchange)
    }
}