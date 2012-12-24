package org.vento.crawler.processor

import com.mongodb.DBObject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.vento.gate.SimpleBatchClassification;
import org.vento.model.Twit;
import org.vento.semantic.sentiment.SentiBatchClassificationImpl
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
    private SimpleBatchClassification evaluationClassifier;

    @Override
    public void process(Exchange exchange) throws Exception {
        //String input = (String) exchange.getIn().getBody();
        /*
        BufferedWriter out = null;
        File temp = null;

        try {
            // Create temp file.
            temp = File.createTempFile("gate-classifier", ".xml");

            // Delete temp file when program exits.
            temp.deleteOnExit();

            // Write to temp file
            out = new BufferedWriter(new FileWriter(temp));
            out.write(input);

        } catch (IOException e) {
            System.out.println("Ma che cazzo!");
        } finally {
            out.close();
        }
*/

        DBObject twit = exchange.getIn().getBody(DBObject.class);

        String origin = (String) exchange.getIn().getHeader("origin");
        Double result = 0.0;

        if (origin.equals("classification")) {

                result = classifier.simpleClassify(twit.get("text"));
                twit.put("type", VentoTypes.CLASSIFICATION);
        }
           else
                if (origin.equals("evaluation")) {
                    result = evaluationClassifier.simpleClassify(twit.get("text"));
                    //twit.put("type", VentoTypes.CLASSIFICATION); //TODO: other type, to be decided later
                }

        twit.put("score", result.toString());

        exchange.getIn().setBody(twit);
    }

    public SimpleBatchClassification getClassifier() {
        return classifier;
    }

    public void setClassifier(SimpleBatchClassification classifier) {
        this.classifier = classifier;
    }

    public SimpleBatchClassification getEvaluationClassifier() {
        return evaluationClassifier;
    }

    public void setEvaluationClassifier(SimpleBatchClassification evaluationClassifier) {
        this.evaluationClassifier = evaluationClassifier;
    }
}
