package org.vento.routes.service;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.twitter.TwitterConstants;

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 11/05/13
 * Time: 19:40
 * To change this template use File | Settings | File Templates.
 */
public class LiveFreeTextClassificationRoute extends RouteBuilder {

    public void configure() {

        from("jetty:http://0.0.0.0:8890/classification")
                .routeId("Live free text classification route")
                .autoStartup(true)
                .processRef("freetextLiveClassificationProcessor");
    }
}