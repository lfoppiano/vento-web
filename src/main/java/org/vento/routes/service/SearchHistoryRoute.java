package org.vento.routes.service;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 02/05/13
 * Time: 18:25
 * To change this template use File | Settings | File Templates.
 */
public class SearchHistoryRoute extends RouteBuilder {


    @EndpointInject(ref = "mongoSearchHistorySave")
    private Endpoint mongoSearchHistorySave;

    public void configure() {

        from("jetty:http://0.0.0.0:8889/searchHistory")
                .routeId("Search history webservice")
                .autoStartup(true)
                .convertBodyTo(String.class)
                .log("SEARCH ITEM: ${body}")
                .choice()
                    .when(body().isNotEqualTo(""))
                        .to("mock:search")
                        .to(mongoSearchHistorySave)
                        .process(new Processor() {

                            @Override
                            public void process(Exchange exchange) throws Exception {

                                System.out.println(exchange.getIn().getBody());

                                exchange.getIn().setHeader("Access-Control-Allow-Origin", "*");
                                exchange.getIn().setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
                            }
                        })
                    .otherwise()
                        .process(new Processor() {

                            @Override
                            public void process(Exchange exchange) throws Exception {

                                System.out.println(exchange.getIn().getBody());

                                exchange.getIn().setHeader("Access-Control-Allow-Origin", "*");
                                exchange.getIn().setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
                            }
                        });


    }
}