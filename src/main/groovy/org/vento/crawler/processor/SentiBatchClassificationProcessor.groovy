package org.vento.crawler.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.vento.gate.SimpleBatchClassification;
import org.vento.model.Twit;
import org.vento.semantic.sentiment.SentiBatchClassificationImpl;

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

        Twit twit = exchange.getIn().getBody(Twit.class);

        Double result = classifier.simpleClassify(twit.getText());

        twit.setScore(result.toString());

        exchange.getIn().setBody(twit);
    }

    public SimpleBatchClassification getClassifier() {
        return classifier;
    }

    public void setClassifier(SimpleBatchClassification classifier) {
        this.classifier = classifier;
    }
}
