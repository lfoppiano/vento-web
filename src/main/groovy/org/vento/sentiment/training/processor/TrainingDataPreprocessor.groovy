package org.vento.sentiment.training.processor

import com.mongodb.DBObject
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.vento.model.GateDocument

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 24/12/12
 * Time: 15:34
 * To change this template use File | Settings | File Templates.
 */
class TrainingDataPreprocessor implements Processor{

    @Override
    public void process(Exchange exchange) throws Exception {
        GateDocument twit = new GateDocument();
        DBObject inBody = (DBObject) exchange.getIn().getBody();

        /** Text With Nodes **/
        GateDocument.TextWithNodes textWithNodes = new GateDocument.TextWithNodes(
                content: [
                        new GateDocument.TextWithNodes.Node(
                                id: 0
                        ),
                        new GateDocument.TextWithNodes.Node(
                                value: ((String) inBody.get("text"))
                        ),
                        new GateDocument.TextWithNodes.Node(
                                id: ((String) inBody.get("text")).size()
                        )
                ],

        )

        twit.textWithNodes = textWithNodes;


        /** GateDocumentFeatures **/
        GateDocument.GateDocumentFeatures.Feature feature = new GateDocument.GateDocumentFeatures.Feature();

        GateDocument.GateDocumentFeatures gateDocumentFeatures = new GateDocument.GateDocumentFeatures();
        gateDocumentFeatures.feature = feature;

        twit.gateDocumentFeatures = gateDocumentFeatures;

        feature.name = new GateDocument.GateDocumentFeatures.Feature.Name()
        feature.name.className = "java.lang.String"
        feature.name.value = "MimeType"

        feature.value = new GateDocument.GateDocumentFeatures.Feature.Value();
        feature.value.className = "java.lang.String"
        feature.value.value = "text/xml"


        /** Annotation Set **/
        GateDocument.AnnotationSet annotationSet = new GateDocument.AnnotationSet(
            name: "Original markups",
            annotation: new GateDocument.AnnotationSet.Annotation(
                    id: 0,
                    type: "text",
                    startNode: 0,
                    endNode: ((String) inBody.get("text")).size(),
                    feature: new GateDocument.AnnotationSet.Annotation.Feature(
                            name: new GateDocument.AnnotationSet.Annotation.Feature.Name(
                                    className: "java.lang.String",
                                    value: "score"
                            ),
                            value: new GateDocument.AnnotationSet.Annotation.Feature.Value(
                                    className: "java.lang.String",
                                    value: inBody.get("score") as float
                            )
                    )
            )
        )
        twit.annotationSet = annotationSet;

        String twitterId = (String) inBody.get("twitterId")
        exchange.getIn().setBody(twit);
        exchange.getIn().setHeader('twitterId', twitterId)
    }
}
