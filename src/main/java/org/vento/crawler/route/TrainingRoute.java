package org.vento.crawler.route;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.codehaus.groovy.runtime.WritableFile;
import org.vento.gate.GateBatchProcessing;
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
public class TrainingRoute extends RouteBuilder {

    @EndpointInject(ref = "rejectLocation")
    private Endpoint rejectEndpoint;

    @EndpointInject(ref = "trainingTemp")
    private Endpoint trainingTemp;

    @Override
    public void configure() throws Exception {

        errorHandler(
                deadLetterChannel(rejectEndpoint)
                        .retryAttemptedLogLevel(LoggingLevel.WARN)
        );

        from("file:src/data/in?fileName=mongoQueryTraining.txt&noop=true&idempotent=false&delay=30000")
                .routeId("Route for training")
                //.setHeader(MongoDbConstants.LIMIT, constant(500))
                .to("mongodb:mongoDb?database=vento&collection=reports&operation=findAll")
                .split(body()).streaming()
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

                        gateBatchProcessing.addAllToCorpus(new URL(trainingTemp.getEndpointUri()), "xml");
                        gateBatchProcessing.perform();
                    }
                });

        /*        .processRef("gateClassifierProcessor")
                .log("${body.get(\"twitterId\")} - ${body.get(\"text\")} - ${body.get(\"score\")}")
                .to("mongodb:mongoDb?database=vento&collection=reports&operation=save");
                */
    }
}
