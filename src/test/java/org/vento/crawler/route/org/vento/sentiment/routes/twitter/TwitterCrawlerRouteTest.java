package org.vento.crawler.route.org.vento.sentiment.routes.twitter;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;
import org.vento.model.Twit;
import org.vento.crawler.twitter.processor.TwitterPreprocessor;

import java.io.InputStream;
import java.util.UUID;

public class TwitterCrawlerRouteTest extends CamelTestSupport {

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;
    
    @Produce(uri = "direct:twitterResponse")
    protected ProducerTemplate templateTwitterResponse;
    
    @EndpointInject(uri = "mock:twitter")
    protected MockEndpoint twitterMockEndpoint;

    @EndpointInject(uri = "mock:output")
    protected MockEndpoint mockOutput;

    @Ignore
    @Test
    public void testTwitterMessageMassaging() throws Exception {
        InputStream body = getClass().getResourceAsStream("../processor/twitter-response-body.json");
        assertNotNull(body);

        mockOutput.expectedMessageCount(15);
        templateTwitterResponse.sendBody(body);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                TwitterPreprocessor twitterPreprocessor = new TwitterPreprocessor();

                from("direct:start").
                        to("log:httpQuery?level=INFO&showHeaders=true").
                        setHeader(Exchange.HTTP_METHOD, constant("GET")).
                        setHeader(Exchange.HTTP_QUERY, simple("q=${body}&lang=en&rpp=100")).
                        to(twitterMockEndpoint);
                
                from("direct:twitterResponse").
                        convertBodyTo(String.class, "UTF-8").
                        process(twitterPreprocessor).
                        split().xpath("//twits/twit").streaming().
                        convertBodyTo(Twit.class).
                        to("log:QueryValue?level=INFO&showHeaders=true").
                        setHeader("CamelFileName").simple(UUID.randomUUID().toString()).
                        to(mockOutput);

            }
        };
    }
}


