package org.vento.sentiment.classification.processor

import org.apache.camel.Processor
import org.apache.camel.Exchange
import org.vento.model.Twit
import org.vento.sentiment.SimpleBatchClassification

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 27/07/12
 * Time: 10.19
 * To change this template use File | Settings | File Templates.
 */
class SentiBatchClassificationStatisticProcessor implements Processor {

    SimpleBatchClassification classifier

    @Override
    void process(Exchange exchange) {
        String input = (String) exchange.getIn().getBody();

        Double result = classifier.simpleClassify(input);

        Twit twit = exchange.getIn().getBody(Twit.class);

        twit.setReferenceScore(result.toString());

        exchange.getIn().setBody(twit);

    }
}
