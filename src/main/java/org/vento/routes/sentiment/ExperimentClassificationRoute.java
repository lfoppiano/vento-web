package org.vento.routes.sentiment;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 20/12/12
 * Time: 18:48
 * To change this template use File | Settings | File Templates.
 */
public class ExperimentClassificationRoute extends RouteBuilder {

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
                .routeId("Exp sentiment extraction")
                .convertBodyTo(String.class)
                .setHeader(MongoDbConstants.LIMIT, constant(BATHC_FETCH_LIMIT))
                .to(mongoFindAllClassification)
                .split(body())
                .to("seda:tmpClassify");

        from("seda:tmpClassify?concurrentConsumers=5")
                .routeId("Exp sentiment classify")
                .processRef("experimentalClassifier")
                .log("${body.get(\"twitterId\")} - ${body.get(\"text\")} - ${body.get(\"score\")}")
                .to(mongoUpdateClassification);
    }
}
