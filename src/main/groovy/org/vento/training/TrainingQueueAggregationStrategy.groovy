package org.vento.training

import org.apache.camel.Exchange
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
            counter = ((Integer) oldExchange.getIn().getBody());
            counter++;
            oldExchange.getIn().setBody(counter);
            return oldExchange;
        } else {
            newExchange.getIn().setBody(counter);
            return newExchange;
        }
    }
}
