package org.vento.crawler.route.org.vento.sentiment.routes.twitter;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class QueryBuilderRouteTest extends CamelTestSupport {

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @EndpointInject(uri= "mock:queue")
    protected MockEndpoint queryQueue;

    @Test
    public void testCountAndVerifyMessages() throws Exception {

        String sentBody= "nike";

        //queryQueue.expectsNoDuplicates();
        queryQueue.expectedMessageCount(15);

        template.sendBody(sentBody);

        queryQueue.assertIsSatisfied();
        
        assertEquals(15, queryQueue.getExchanges().size());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder(){

            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .split().tokenize("\n").loop(15).copy()
                        .transform(body().append(simple("&page=${header.CamelLoopIndex}++")))
                        .to(queryQueue);
            }
        };
    }
}