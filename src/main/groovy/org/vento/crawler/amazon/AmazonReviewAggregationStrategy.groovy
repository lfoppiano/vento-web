package org.vento.crawler.amazon

import org.apache.camel.Exchange
import org.apache.camel.processor.aggregate.AggregationStrategy

/**
 * Created by IntelliJ IDEA.
 * User: lfoppiano
 * Date: 3/18/12
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class AmazonReviewAggregationStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange original, Exchange resource) {
        original.getIn().setHeader("maxPageNumber", resource.getIn().getHeader("maxPageNumber"))

        return original;
    }
}
