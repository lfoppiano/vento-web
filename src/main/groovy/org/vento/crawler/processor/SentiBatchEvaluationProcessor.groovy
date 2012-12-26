package org.vento.crawler.processor

import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.vento.gate.SimpleBatchClassification
import org.vento.utility.VentoTypes;

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 26/12/12
 * Time: 22:19
 * To change this template use File | Settings | File Templates.
 */
public class SentiBatchEvaluationProcessor implements Processor {

    private SimpleBatchClassification classifier;

    @Override
    public void process(Exchange exchange) throws Exception {

        DBObject element = exchange.getIn().getBody(DBObject.class)

        Double result = classifier.simpleClassify(element.get("text"));
        element.put("type", VentoTypes.TESTING) //maybe not neccessary, it should be already set
        BasicDBObject tempScore = new BasicDBObject()
        tempScore.put("value", result)
        tempScore.put("timestamp", exchange.getIn().getHeader("timestamp"))

        def tmpHistoryList = (List) element.get("score-history")
        if (!tmpHistoryList) tmpHistoryList = [] //should be set before already, if not, this is
        element.put("score-history", tmpHistoryList.add(tempScore)) //a failsafe

        exchange.getIn().setBody(element);
    }

    public void setClassifier(SimpleBatchClassification classifier) {
        this.classifier = classifier;
    }
}
