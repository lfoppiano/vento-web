package org.vento.sentiment.classification.processor

import com.mongodb.DBObject
import gate.Annotation
import gate.Document
import gate.Factory
import gate.util.DocumentProcessor
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.vento.sentiment.classification.ClassificationWrapper
import org.vento.utility.VentoTypes

//import gate.creole.SerialAnalyserController
/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 22/07/12
 * Time: 21.25
 * To change this template use File | Settings | File Templates.
 */
public class SentiBatchClassificationProcessor implements Processor {

    private ClassificationWrapper classificationWrapper;

    @Override
    public void process(Exchange exchange) throws Exception {

        def result = 0.0

        DBObject twit = exchange.getIn().getBody(DBObject.class)

        result = classificationWrapper.classify(twit.get("text"))

        twit.put("score", result.toString())
        twit.put("type", VentoTypes.CLASSIFICATION)

        exchange.getIn().setBody(twit)
    }

    void setClassificationWrapper(ClassificationWrapper classificationWrapper) {
        this.classificationWrapper = classificationWrapper
    }


}
