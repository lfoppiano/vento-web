package org.vento.sentiment.classification.processor

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.vento.model.Twit
import org.vento.sentiment.classification.ClassificationWrapper
import org.vento.utility.StringProcessor
import org.vento.utility.TwitHelper

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 19/01/13
 * Time: 17:13
 * To change this template use File | Settings | File Templates.
 */
public class FreeTextLiveClassificationProcessor implements Processor {
    private ClassificationWrapper classificationWrapper;

    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);

        Twit t = new Twit();
        t.setText(StringProcessor.textProcessing(body));
        t.setScore("" + classificationWrapper.classify(t.getText()));

        def jsonBuilder = new groovy.json.JsonBuilder(t);

        exchange.getOut().setBody(jsonBuilder.toString());
        exchange.getOut().setHeader("Access-Control-Allow-Origin", "*")
    }

    void setClassificationWrapper(ClassificationWrapper classificationWrapper) {
        this.classificationWrapper = classificationWrapper
    }

}
