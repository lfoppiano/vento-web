package org.vento.routes.sentiment;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 20/12/12
 * Time: 18:48
 * To change this template use File | Settings | File Templates.
 */
public class ClassificationRoute extends RouteBuilder {

    private final int BATHC_FETCH_LIMIT = 1000;
    @EndpointInject(ref = "rejectLocation")
    private Endpoint rejectEndpoint;
    @EndpointInject(ref = "mongoQueryClassification")
    private Endpoint mongoQueryClassification;
    @EndpointInject(ref = "mongoStorageFindAll")
    private Endpoint mongoFindAllClassification;
    @EndpointInject(ref = "mongoStorageSave")
    private Endpoint mongoUpdateClassification;

    @Override
    public void configure() throws Exception {

        /*errorHandler(
                deadLetterChannel(rejectEndpoint)
                        .retryAttemptedLogLevel(LoggingLevel.WARN)
        );*/

        from(mongoQueryClassification)
                .routeId("Classification sentiment extraction")
                .convertBodyTo(String.class)
                .setHeader(MongoDbConstants.LIMIT, constant(BATHC_FETCH_LIMIT))
                .to(mongoFindAllClassification)
                .split(body())
                .to("seda:tmpClassify");

        from("seda:tmpClassify?concurrentConsumers=5")
                .routeId("Classification sentiment classify")
                .processRef("gateClassificationProcessor")
                .log("${body.get(\"twitterId\")} - ${body.get(\"text\")} - ${body.get(\"score\")}")
                .to(mongoUpdateClassification);

        /*from(mongoQueryClassification)
                .routeId("Sentiment classification")
                .convertBodyTo(String.class)
                .setHeader(MongoDbConstants.LIMIT, constant(BATHC_FETCH_LIMIT))
                .to(mongoFindAllClassification)
                .split(body())
                .processRef("gateClassifierProcessor")
                .log("${body.get(\"twitterId\")} - ${body.get(\"text\")} - ${body.get(\"score\")}")
                .to(mongoUpdateClassification);
                */
    }
}
