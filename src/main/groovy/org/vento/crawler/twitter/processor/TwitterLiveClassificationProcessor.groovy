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

    //private SimpleBatchClassification classifier;
    private ConditionalSerialAnalyserController classifier;

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
            t.setScore("" + classify(t.getText()));
            ts.getTwits().add(t);
        }

        def twits = []
        for (Twit tweet : ts.twits) {
            def twit = [
                    tweet.getTwitterId(),
                    tweet.getText(),
                    tweet.getScore(),
                    tweet.getReferenceScore()
            ]
            twits << twit
        }

        def dataToRender = [:]
        dataToRender['aaData'] = twits
        dataToRender['iTotalDisplayRecords'] = ts.twits.size()
        dataToRender['iTotalRecords'] = ts.twits.size()


        def jsonBuilder = new groovy.json.JsonBuilder(dataToRender);

        exchange.getOut().setBody(jsonBuilder.toString());
        exchange.getOut().setHeader("Access-Control-Allow-Origin", "*")
    }

    public void setClassifier(ConditionalSerialAnalyserController classifier) {
        this.classifier = classifier;
    }

    private float classify(String text) {
        float result = 0.0
        Corpus tmpCorpus = gate.Factory.newCorpus(Gate.genSym())
        tmpCorpus.add(gate.Factory.newDocument("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<twit>\n<text>" + text + "</text>\n</twit>"))
        classifier.setCorpus(tmpCorpus)

        classifier.execute()
        Iterator<Annotation> classificationScoreStr = ((Document)tmpCorpus.iterator().next()).getAnnotations("Output").get("Review").iterator()
        if (classificationScoreStr.hasNext()){

            result = Float.parseFloat((String)classificationScoreStr.next().getFeatures().get("score"))
        }

        tmpCorpus.clear();
        tmpCorpus.cleanup();
        gate.Factory.deleteResource(tmpCorpus);

        return result;
    }

}
