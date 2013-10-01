package org.vento.routes.sentiment;

import com.mongodb.DBObject;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.vento.sentiment.training.TrainingQueueAggregationStrategy;
import org.vento.utility.VentoTypes;


/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 20/12/12
 * Time: 18:48
 * To change this template use File | Settings | File Templates.
 */
public class TrainingRoute extends RouteBuilder {

    private final int QUERY_FETCH_LIMIT = 1253;
    @EndpointInject(ref = "rejectLocation")
    private Endpoint rejectEndpoint;
    @EndpointInject(ref = "trainingTemp")
    private Endpoint trainingTemp;
    @EndpointInject(ref = "mongoQueryTraining")
    private Endpoint mongoQueryTraining;
    @EndpointInject(ref = "mongoStorageFindAll")
    private Endpoint mongoConnector;
    @EndpointInject(ref = "mongoStorageSave")
    private Endpoint storageTypeUpdate;
    //TODO: make it configurable
    private int trainingBatchLimit = QUERY_FETCH_LIMIT;

    @Override
    public void configure() throws Exception {

        /*errorHandler(
                deadLetterChannel(rejectEndpoint)
                        .retryAttemptedLogLevel(LoggingLevel.WARN)
        );*/

        from(mongoQueryTraining)
                .routeId("Training route")
                .convertBodyTo(String.class)
                .setHeader(MongoDbConstants.LIMIT, constant(QUERY_FETCH_LIMIT))
                .to(mongoConnector)
                .split(body())
                //.log("${body.get(\"twitterId\")} - ${body.get(\"text\")} - ${body.get(\"score\")} - ${body.get(\"type\")} ")
                .processRef("trainingDataPreprocessor")
                .to(trainingTemp)
                .setHeader("aggregationId", constant("bao"))
                .aggregate(header("aggregationId"), new TrainingQueueAggregationStrategy()).completionSize(trainingBatchLimit)
                //.log("I have finished to aggregate 1000 elements! Run the training! ${body}")
                //.processRef("gateTrainingProcessor")
                .processRef("trainingWrapperProcessor")
                .log("Finish training! Updating stored data.");
                /*.split().tokenize("\n")
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
                .to(storageTypeUpdate);*/
    }

    public void setTrainingBatchLimit(int trainingBatchLimit) {
        this.trainingBatchLimit = trainingBatchLimit;
    }
}
