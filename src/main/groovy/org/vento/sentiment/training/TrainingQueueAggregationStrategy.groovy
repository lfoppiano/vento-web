package org.vento.sentiment.training

import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.processor.aggregate.AggregationStrategy

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 24/12/12
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public class TrainingQueueAggregationStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Integer counter = 1;

        if (oldExchange != null) {
            Message input = oldExchange.getIn()
            counter = ((Integer) input.getHeader('counter'));
            counter++;
            input.setHeader('counter', counter);
            String newBody = input.getBody(String.class) + "\n" + newExchange.getIn().getHeader('twitterId', String.class);
            input.setBody(newBody)
            return oldExchange;
        } else {
            newExchange.getIn().setHeader('counter', counter);
            Message input = newExchange.getIn()
            input.setBody(input.getHeader('twitterId', String.class));
            return newExchange;
        }
    }
}
