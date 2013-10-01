package org.vento.routes.sentiment;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;

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

    private final int BATH_FETCH_LIMIT = 200;

    @Override
    public void configure() throws Exception {

        /*errorHandler(
                deadLetterChannel(rejectEndpoint)
                        .retryAttemptedLogLevel(LoggingLevel.WARN)
        );*/

        from(mongoQueryTesting)
                .routeId("Testing (evaluation) route")
                .convertBodyTo(String.class)
                .setHeader("timestamp").simple("${date:now:yyyyMMdd hh:mm:ss}") //has to be set only once, not sure how to do it now
                .setHeader(MongoDbConstants.LIMIT, constant(BATH_FETCH_LIMIT))
                .to(mongoConnector)
                .split(body())
                .log("${body.get(\"twitterId\")} - ${body.get(\"text\")} - ${body.get(\"score\")} - ${body.get(\"type\")} ")
                .setHeader("origin").simple("evaluation")
                .processRef("gateEvaluationProcessor")
                .to(mongoUpdateTesting);
    }
}
