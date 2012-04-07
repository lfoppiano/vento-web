/*
package org.bao.crawler;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

*/
/**
 * Created by IntelliJ IDEA.
 * User: lf84914
 * Date: 2/17/12
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 *//*

public class CrawlerRouteWithAdviceTest extends CamelTestSupport {

    @EndpointInject(uri = "direct:start")
    private Endpoint sourceFileQuery;

    @EndpointInject(uri = '' )
    private Endpoint queryQueue;


    @Test
    public void testAdvisedMockEndpoints() throws Exception {

        context.getRouteDefinitions().get(0).adviceWith(context, new
                AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        mockEndpoints();
                    }
                });

        getMockEndpoint("mock:direct:start").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:direct:foo").expectedBodiesReceived("Hello World");

        getMockEndpoint("mock:log:foo").expectedBodiesReceived("Bye World");
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye World");
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();

        assertNotNull(context.hasEndpoint("direct:start"));
        assertNotNull(context.hasEndpoint("direct:foo"));
        assertNotNull(context.hasEndpoint("log:foo"));
        assertNotNull(context.hasEndpoint("mock:result"));

        assertNotNull(context.hasEndpoint("mock:direct:start"));
        assertNotNull(context.hasEndpoint("mock:direct:foo"));
        assertNotNull(context.hasEndpoint("mock:log:foo"));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {

        return new CrawlerRoute();
    }
}
*/
