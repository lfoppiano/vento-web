package org.vento.sentiment.classification

import com.mongodb.DBObject
import gate.Annotation
import gate.Document
import gate.Factory
import gate.util.DocumentProcessor
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.vento.utility.VentoTypes

//import gate.creole.SerialAnalyserController
/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 22/07/12
 * Time: 21.25
 * To change this template use File | Settings | File Templates.
 */
public class SentiBatchExperimentalClassificationProcessor implements Processor {

    //private ConditionalSerialAnalyserController classifier;
    private DocumentProcessor classifier;

    @Override
    public void process(Exchange exchange) throws Exception {

        def result = 0.0
        Document tmpDocument = null;
        DBObject twit = exchange.getIn().getBody(DBObject.class)

        try {
            tmpDocument = Factory.newDocument(("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<twit>\n<text>" + twit.get("text") + "</text>\n</twit>"))
            classifier.processDocument(tmpDocument);

            Iterator<Annotation> classificationScoreStr = tmpDocument.getAnnotations("Output").get("Review").iterator()
            if (classificationScoreStr.hasNext()) {
                result = Float.parseFloat((String) classificationScoreStr.next().getFeatures().get("score"))
            }
            Factory.deleteResource(tmpDocument)
        } finally {
            //tmpCorpus.clear();
            //tmpCorpus.cleanup();
            //Factory.deleteResource(tmpCorpus);
            //classifier.cleanup()
            Factory.deleteResource(tmpDocument);
            tmpDocument = null;
        }
        twit.put("score", result.toString())
        twit.put("type", VentoTypes.CLASSIFICATION)

        exchange.getIn().setBody(twit)
    }


    public void setClassifier(DocumentProcessor classifier) {
        this.classifier = classifier;
    }
}
