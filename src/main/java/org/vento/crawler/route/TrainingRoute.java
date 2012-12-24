package org.vento.crawler.route;

import com.mongodb.DBObject;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.vento.gate.GateBatchProcessing;
import org.vento.semantic.sentiment.SentiBatchProcessingImpl;
import org.vento.training.TrainingQueueAggregationStrategy;
import org.vento.utility.VentoTypes;

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

    @EndpointInject(ref = "mongoQueryTraining")
    private Endpoint mongoQueryTraining;

    @EndpointInject(ref = "mongoConnector")
    private Endpoint mongoConnector;

    @EndpointInject(ref = "storageTypeUpdate")
    private Endpoint storageTypeUpdate;


    @Override
    public void configure() throws Exception {

        /*errorHandler(
                deadLetterChannel(rejectEndpoint)
                        .retryAttemptedLogLevel(LoggingLevel.WARN)
        );*/

        from(mongoQueryTraining)
                .routeId("Training input extraction")
                .convertBodyTo(String.class)
                .setHeader(MongoDbConstants.LIMIT, constant(10))
                .to(mongoConnector)
                .split(body())
                .log("${body.get(\"twitterId\")} - ${body.get(\"text\")} - ${body.get(\"score\")} - ${body.get(\"type\")} ")
                .processRef("trainingDataPreprocessor")
                .to(trainingTemp)
                .setHeader("aggregationId", constant("bao"))
                .aggregate(header("aggregationId"), new TrainingQueueAggregationStrategy()).completionSize(10)
                .log("I have finished to aggregate 1000 elements! Run the training! ${body}")
                .process(new Processor() {
                    private GateBatchProcessing gateBatchProcessing;

                    @Override
                    //TODO: all paths from windows, have to be changed and put into config
                    public void process(Exchange exchange) throws Exception {
                        String dataStoreDir = "file:/tmp/twitter/tempTrainingStore";

                        gateBatchProcessing = new SentiBatchProcessingImpl(
                                //new File("/Applications/GATE_Developer_7.0"),
                                new File("/Applications/gate-7.1-build4485-BIN"),
                                new File("/opt/local/gate-training/batch-learning.training.configuration.xml"),
                                dataStoreDir,
                                "trainingCorpus");

                        gateBatchProcessing.addAllToCorpus(new URL("file:/tmp/twitter/training"), "xml");
                        gateBatchProcessing.perform();
                    }
                })
                .log("Finish training! Updating stored data.")
                .split().tokenize("\n")
                .log("Processing ${body}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setBody("{ \"twitterId\" : \"" + exchange.getIn().getBody() + "\"}", String.class);
                    }
                })
                .convertBodyTo(String.class)
                .log("Retrieving query ${body}")
                .to(mongoConnector)
                .split(body())
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        DBObject inBody = (DBObject) exchange.getIn().getBody();
                        inBody.put("type", VentoTypes.TRAINING_STORESET);
                    }
                })
                .to(storageTypeUpdate);
    }
}
