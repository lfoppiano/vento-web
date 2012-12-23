package org.vento.crawler.route;

import com.mongodb.DBObject;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.codehaus.groovy.runtime.WritableFile;
import org.vento.gate.GateBatchProcessing;
import org.vento.model.Twit;
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
                .routeId("Sentiment training")
                .convertBodyTo(String.class)
                .setHeader(MongoDbConstants.LIMIT, constant(500))
                .to(mongoConnector)
                .split(body())
                .log("${body.get(\"twitterId\")} - ${body.get(\"text\")} - ${body.get(\"score\")} - ${body.get(\"type\")} ")
                //.convertBodyTo(String.class)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Twit twit = new Twit();
                        DBObject inBody = (DBObject) exchange.getIn().getBody();
                        twit.setText((String) inBody.get("text"));
                        twit.setTwitterId((String) inBody.get("twitterId"));
                        exchange.getIn().setBody(twit);
                    }
                })
                .to(trainingTemp);
                /*.process(new Processor() {
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
                });*/
    }
}
