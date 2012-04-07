package org.vento.crawler.route;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.vento.crawler.processor.TwitterPreprocessor;
import org.vento.model.Twit;

import java.util.UUID;


/**
 * Created by IntelliJ IDEA.
 * User: lfoppiano
 * Date: 2/10/12
 * Time: 4:57 PM
 * To change this template use File | Settings | File Templates.
 */

public class TwitterCrawlerRoute extends RouteBuilder {


    @EndpointInject(ref = "sourceFileQuery")
    private Endpoint sourceFileQuery;

    @EndpointInject(ref = "queryQueue")
    private Endpoint queryQueue;

    @EndpointInject(ref = "outputDirectory")
    private Endpoint outputDirectory;

    public void configure(){

        TwitterPreprocessor twitterPreprocessor = new TwitterPreprocessor();
        from(sourceFileQuery)
                .autoStartup(false)
                .routeId("TwitterUrlBuilder")
                .split().tokenize("\n")
                .loop(15).copy()
                .transform(body().append(simple("&page=${header.CamelLoopIndex}++")))
                .to(queryQueue);

        from(queryQueue)
                .autoStartup(false)
                .routeId("TwitterCrawler")
                //.to("log:httpQuery?level=INFO&showHeaders=true")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader(Exchange.HTTP_QUERY, simple("q=${body}&lang=en&rpp=100"))
                .to("http://search.twitter.com/search.json")
                .convertBodyTo(String.class, "UTF-8")
                .process(twitterPreprocessor)
                .split().xpath("//twits/twit").streaming()
                .convertBodyTo(Twit.class)
                //.to("log:QueryValue?level=INFO&showHeaders=true")
                .setHeader("CamelFileName").simple(UUID.randomUUID().toString())
                .to(outputDirectory);

    }

}
