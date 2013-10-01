package org.vento.routes.sentiment;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;

/**
 * Created with IntelliJ IDEA.
 * User: Martin
 * Date: 01/06/2013
 * Time: 12:26
 * To change this template use File | Settings | File Templates.
 */
public class CalculationRoute extends RouteBuilder {

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

    private final int BATH_FETCH_LIMIT = 300;

    @Override
    public void configure() throws Exception {

        from(mongoQueryTesting)
                .routeId("Testing (calculation) route")
                .convertBodyTo(String.class)
                .setHeader(MongoDbConstants.LIMIT, constant(BATH_FETCH_LIMIT))
                .to(mongoConnector)
                .setHeader("origin").simple("calculation")
                .processRef("ventoCalculationProcessor");
    }
}
