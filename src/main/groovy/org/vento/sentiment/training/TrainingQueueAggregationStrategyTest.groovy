package org.vento.sentiment.training

import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.impl.DefaultMessage
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 24/12/12
 * Time: 21:28
 * To change this template use File | Settings | File Templates.
 */
public class TrainingQueueAggregationStrategyTest {

    TrainingQueueAggregationStrategy target;
    Exchange oldExchange;
    Exchange newExchange;

    @Before
    public void setUp() throws Exception {
        target = new TrainingQueueAggregationStrategy();

        oldExchange = new DefaultExchange(new DefaultCamelContext());
        Message oldMessage = new DefaultMessage();
        oldMessage.setHeader('twitterId', 'baoOld');
        oldMessage.setHeader('counter', 1);
        oldMessage.setBody("baoOld")
        oldExchange.setIn(oldMessage);

        newExchange = new DefaultExchange(new DefaultCamelContext());
        Message newMessage = new DefaultMessage();
        newMessage.setHeader('twitterId', 'baoNew');
        newMessage.setHeader('counter', 1);
        newExchange.setIn(newMessage)

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAggregateFirstMessage() throws Exception {

        Exchange result = target.aggregate(null, newExchange);

        assert result != null;
        assert result.getIn().getHeader('counter') != null
        assert result.getIn().getHeader('counter', Integer.class) == 1
        assert result.getIn().getBody() != null
        assert result.getIn().getBody().trim() == 'baoNew'
    }

    @Test
    public void testAggregateFollowingMessages() throws Exception {
        Exchange result = target.aggregate(oldExchange, newExchange);

        assert result != null;
        assert result.getIn().getHeader('counter') != null
        assert result.getIn().getHeader('counter', Integer.class) == 2
        assert result.getIn().getBody() != null
        assert result.getIn().getBody() == 'baoOld' + '\n' + 'baoNew'
    }

    @Test
    public void testAggregate123Messages() throws Exception {
        Exchange result1 = target.aggregate(oldExchange, newExchange);
        Exchange result2 = target.aggregate(result1, newExchange);
        Exchange result3 = target.aggregate(result2, newExchange);

        assert result3.getIn().getBody() == 'baoOld' + '\n' + 'baoNew' + '\n' + 'baoNew' + '\n' + 'baoNew'
    }
}

