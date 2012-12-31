package org.vento.sentiment.classification

import com.mongodb.DBObject
import gate.Annotation
import gate.Corpus
import gate.Gate
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.vento.utility.VentoTypes

import gate.Document
import gate.Factory
//import gate.creole.SerialAnalyserController
import gate.creole.ConditionalSerialAnalyserController
import gate.corpora.DocumentContentImpl

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 22/07/12
 * Time: 21.25
 * To change this template use File | Settings | File Templates.
 */
public class SentiBatchExperimentalClassificationProcessor implements Processor {

    private ConditionalSerialAnalyserController classifier;

    @Override
    public void process(Exchange exchange) throws Exception {

        def result = 0.0
        Corpus tmpCorpus

        DBObject twit = exchange.getIn().getBody(DBObject.class)

        tmpCorpus = Factory.newCorpus(Gate.genSym())
        tmpCorpus.add(Factory.newDocument("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<twit>\n<text>" + twit.get("text") + "</text>\n</twit>"))
        classifier.setCorpus(tmpCorpus)

   //     DocumentContentImpl doc = classifier.getDocument().getContent()

    //    doc.getOriginalContent()

        classifier.execute()
        Iterator<Annotation> classificationScoreStr = ((Document)tmpCorpus.iterator().next()).getAnnotations("Output").get("Review").iterator()
        if (classificationScoreStr.hasNext()){

            result = Float.parseFloat((String)classificationScoreStr.next().getFeatures().get("score"))
        }

        tmpCorpus.clear();
        tmpCorpus.cleanup();
        Factory.deleteResource(tmpCorpus);

        twit.put("score", result.toString())
        twit.put("type", VentoTypes.CLASSIFICATION)

        exchange.getIn().setBody(twit)
    }


    public void setClassifier(ConditionalSerialAnalyserController classifier) {
        this.classifier = classifier;
    }
}
