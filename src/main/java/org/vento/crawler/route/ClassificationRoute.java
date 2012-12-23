package org.vento.crawler.route;

import gate.creole.ExecutionException;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.LoggingLevel;
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

    @EndpointInject(ref = "rejectLocation")
    private Endpoint rejectEndpoint;

    @EndpointInject(ref = "mongoQueryClassification")
    private Endpoint mongoQueryClassification;

    @EndpointInject(ref = "mongoFindAllClassification")
    private Endpoint mongoFindAllClassification;

    @EndpointInject(ref = "mongoUpdateClassification")
    private Endpoint mongoUpdateClassification;

    @Override
    public void configure() throws Exception {

        errorHandler(
                deadLetterChannel(rejectEndpoint)
                        .retryAttemptedLogLevel(LoggingLevel.WARN)
        );

        from(mongoQueryClassification)
                .routeId("Sentiment classification")
                .convertBodyTo(String.class)
                .setHeader(MongoDbConstants.LIMIT, constant(500))
                .to(mongoFindAllClassification)
                .split(body())
                .processRef("gateClassifierProcessor")
                .log("${body.get(\"twitterId\")} - ${body.get(\"text\")} - ${body.get(\"score\")}")
                .to(mongoUpdateClassification);
    }
}
