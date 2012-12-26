package org.vento.crawler.route;

import com.mongodb.DBObject;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.vento.gate.GateBatchProcessing;
import org.vento.model.ScoreHistory;
import org.vento.model.Twit;
import org.vento.semantic.sentiment.SentiBatchProcessingImpl;

import javax.xml.datatype.XMLGregorianCalendar;
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
                .setHeader(MongoDbConstants.LIMIT, constant(10))
                .to(mongoConnector)
                .split(body())
                .log("${body.get(\"twitterId\")} - ${body.get(\"text\")} - ${body.get(\"score\")} - ${body.get(\"type\")} ")
                .setHeader("origin").simple("evaluation")
                .processRef("gateClassifierProcessor")
                /*.convertBodyTo(Twit.class)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Twit element = (Twit) exchange.getIn().getBody();

                        ScoreHistory scoreElement = new ScoreHistory();
                        //scoreElement.setTimestamp();
                        scoreElement.setValue(element.getScore());

                        element.getScoreHistories().add(scoreElement);
                    }
                })*/
                .to(mongoUpdateTesting);
    }
}
