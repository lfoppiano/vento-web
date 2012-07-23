package org.vento.crawler.route;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.vento.crawler.processor.TwitterPreprocessor;
import org.vento.model.Twit;
import org.vento.model.Twits;

import java.io.File;
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

    @EndpointInject(ref = "outputDirectory")
    private Endpoint outputDirectory;

    @EndpointInject(ref = "rejectTwitterLocation")
    private Endpoint twitterRejectEndpoint;

    public void configure(){

        errorHandler(
                deadLetterChannel(twitterRejectEndpoint)
                        .retryAttemptedLogLevel(LoggingLevel.WARN)
        );

        onException(java.net.SocketException.class)
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .maximumRedeliveries(5)
                .redeliveryDelay(5000);
        
        onException(org.apache.commons.httpclient.NoHttpResponseException.class)
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .maximumRedeliveries(5)
                .redeliveryDelay(5000);

        //TwitterPreprocessor twitterPreprocessor = new TwitterPreprocessor();

        from(sourceFileQuery)
                .routeId("TwitterUrlBuilder")
                .split().tokenize("\n")
                .loop(15).copy()
                .transform(body().append(simple("&page=${header.CamelLoopIndex}++")))
                .to("seda:queryQueue");

        from("seda:queryQueue?concurrentConsumers=1")
                .routeId("TwitterCrawler")
                //.to("log:httpQuery?level=INFO&showHeaders=true")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader(Exchange.HTTP_QUERY, simple("q=${body}&lang=en&rpp=100"))
                .to("http://search.twitter.com/search.json?httpClient.cookiePolicy=ignoreCookies")
                .convertBodyTo(String.class, "UTF-8")
                .processRef("twitterPreprocessor")
                .convertBodyTo(Twits.class)
                .split().xpath("//twits/twit").streaming()
                .convertBodyTo(String.class)
                //.to("log:QueryValue?level=INFO&showHeaders=true")
                .processRef("gateClassifierProcessor")
                .setHeader("CamelFileName").simple(UUID.randomUUID().toString())
                .to(outputDirectory);

    }

}
