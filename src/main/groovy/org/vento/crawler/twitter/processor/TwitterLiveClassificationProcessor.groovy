package org.vento.crawler.twitter.processor

import gate.Annotation
import gate.Corpus
import gate.Document
import gate.Gate
import gate.creole.ConditionalSerialAnalyserController
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.vento.model.Twit
import org.vento.model.Twits
import org.vento.sentiment.SimpleBatchClassification
import org.vento.sentiment.classification.ClassificationWrapper
import org.vento.utility.StringProcessor
import org.vento.utility.TwitHelper
import twitter4j.Status

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 19/01/13
 * Time: 17:13
 * To change this template use File | Settings | File Templates.
 */
public class TwitterLiveClassificationProcessor implements Processor {
    private ClassificationWrapper classificationWrapper;

    @Override
    public void process(Exchange exchange) throws Exception {
        List list = (List) exchange.getIn().getBody();
        Iterator i = list.iterator();

        Twits ts = new Twits();
        while (i.hasNext()) {
            Status tweet = (Status) i.next();
            Twit t = new Twit();
            t.setTwitterId("" + tweet.getId());
            t.setText(tweet.getText());
            t = TwitHelper.analyzeAndCleanEmotions(t);
            t.setText(StringProcessor.textProcessing(tweet.getText()));
            t.setScore("" + classificationWrapper.classify(t.getText()));
            ts.getTwits().add(t);
        }

        def twits = []
        for (Twit tweet : ts.twits) {
            def twit = [
                    twitterId: tweet.getTwitterId(),
                    text: tweet.getText(),
                    score: tweet.getScore(),
                    referenceScore: tweet.getReferenceScore()
            ]
            twits << twit
        }

        /*def dataToRender = [:]
        dataToRender['aaData'] = twits
        dataToRender['iTotalDisplayRecords'] = ts.twits.size()
        dataToRender['iTotalRecords'] = ts.twits.size()*/


        def jsonBuilder = new groovy.json.JsonBuilder(twits);

        exchange.getOut().setBody(jsonBuilder.toString());
        exchange.getOut().setHeader("Access-Control-Allow-Origin", "*")
    }

    void setClassificationWrapper(ClassificationWrapper classificationWrapper) {
        this.classificationWrapper = classificationWrapper
    }

}
