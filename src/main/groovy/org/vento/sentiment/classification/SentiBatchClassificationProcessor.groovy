package org.vento.sentiment.classification

import com.mongodb.DBObject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor
import org.vento.sentiment.SimpleBatchClassification;
import org.vento.utility.VentoTypes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;import java.lang.*;import java.lang.Exception;import java.lang.Override;import java.lang.String;import java.lang.System;

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 22/07/12
 * Time: 21.25
 * To change this template use File | Settings | File Templates.
 */
public class SentiBatchClassificationProcessor implements Processor {

    private SimpleBatchClassification classifier;

    @Override
    public void process(Exchange exchange) throws Exception {

        DBObject twit = exchange.getIn().getBody(DBObject.class);

        Double result = classifier.simpleClassify(twit.get("text"));

        twit.put("score", result.toString());
        twit.put("type", VentoTypes.CLASSIFICATION)

        exchange.getIn().setBody(twit);
    }


    public void setClassifier(SimpleBatchClassification classifier) {
        this.classifier = classifier;
    }
}
