package org.vento.crawler.route;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.vento.crawler.processor.TwitterPreprocessor;

public class QueryBuilderRoute extends RouteBuilder {

    @EndpointInject(ref = "sourceFileQuery")
    private Endpoint sourceFileQuery;

    @EndpointInject(ref = "queryQueue")
    private Endpoint queryQueue;

    public void configure() {
        TwitterPreprocessor twitterPreprocessor = new TwitterPreprocessor();

        from(sourceFileQuery)
                .autoStartup(false)
                .split().tokenize("\n")
                .loop(15).copy()
                .transform(body().append(simple("&page=${header.CamelLoopIndex}++")))
                .to(queryQueue);


    }
}

