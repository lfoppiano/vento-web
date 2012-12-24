package org.vento.crawler.route;

import com.mongodb.DBObject;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.vento.gate.GateBatchProcessing;
import org.vento.model.Twit;
import org.vento.semantic.sentiment.SentiBatchProcessingImpl;

import java.io.File;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 20/12/12
 * Time: 18:48
 * To change this template use File | Settings | File Templates.
 */
public class TestingRoute extends RouteBuilder {

/*
    @EndpointInject(ref = "trainingTemp")
    private Endpoint trainingTemp;

*/
    @EndpointInject(ref = "mongoStorageFindAll")
    private Endpoint mongoConnector;

    @EndpointInject(ref = "rejectLocation")
    private Endpoint rejectEndpoint;

    @EndpointInject(ref = "mongoQueryTesting")
    private Endpoint mongoQueryTesting;

    @EndpointInject(ref = "mongoStorageSave")
    private Endpoint mongoUpdateTesting;

    @Override
    public void configure() throws Exception {

        /*errorHandler(
                deadLetterChannel(rejectEndpoint)
                        .retryAttemptedLogLevel(LoggingLevel.WARN)
        );*/

        from(mongoQueryTesting)
                .routeId("Testing (evaluation) route")
                .convertBodyTo(String.class)
                .setHeader(MongoDbConstants.LIMIT, constant(500))
                .to(mongoConnector)
                .split(body())
                .log("${body.get(\"twitterId\")} - ${body.get(\"text\")} - ${body.get(\"score\")} - ${body.get(\"type\")} ")
                .setHeader("origin")
                .simple("evaluation")
                .processRef("gateClassifierProcessor")
                .to(mongoUpdateTesting);
    }
}
