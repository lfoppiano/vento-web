package org.vento.crawler.route;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.codehaus.groovy.runtime.WritableFile;
import org.vento.gate.GateBatchProcessing;
import org.vento.semantic.sentiment.SentiBatchProcessingImpl;

import java.io.File;
import java.net.URL;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 20/12/12
 * Time: 18:48
 * To change this template use File | Settings | File Templates.
 */
public class TrainingRoute extends RouteBuilder {

    @EndpointInject(ref = "rejectLocation")
    private Endpoint rejectEndpoint;

    @EndpointInject(ref = "trainingTemp")
    private Endpoint trainingTemp;

    @EndpointInject(ref = "mongoQueryTraining")
    private Endpoint mongoQueryTraining;

    @EndpointInject(ref = "mongoConnector")
    private Endpoint mongoConnector;

    @Override
    public void configure() throws Exception {

        errorHandler(
                deadLetterChannel(rejectEndpoint)
                        .retryAttemptedLogLevel(LoggingLevel.WARN)
        );

        from(mongoQueryTraining)
                .routeId("Route for training")
                .convertBodyTo(String.class)
                .setHeader(MongoDbConstants.LIMIT, constant(1))
                .to(mongoConnector)
                .split(body())
                .log("${body.get(\"twitterId\")} - ${body.get(\"text\")} - ${body.get(\"score\")} - ${body.get(\"type\")} ")
                .setHeader("CamelFileName").simple(UUID.randomUUID().toString()+".xml")
                .to(trainingTemp)
                .process(new Processor() {
                    private GateBatchProcessing gateBatchProcessing;

                    @Override
                    public void process(Exchange exchange) throws Exception {
                        gateBatchProcessing = new SentiBatchProcessingImpl(
                                new File("/Applications/GATE_Developer_7.0"),
                                new File("/opt/local/gate-training/batch-learning.training.configuration.xml"),
                                "/tmp/twitter/",
                                "trainingCorpus");

                        gateBatchProcessing.addAllToCorpus(new URL("/tmp/twitter/training"), "xml");
                        gateBatchProcessing.perform();
                    }
                });
    }
}
