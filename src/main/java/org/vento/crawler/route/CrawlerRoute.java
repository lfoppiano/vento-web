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

public class CrawlerRoute extends RouteBuilder {

    @EndpointInject(ref = "outputDirectory")
    private Endpoint outputDirectory;

    @EndpointInject(ref = "queryQueue")
    private Endpoint queryQueue2;

    public void configure(){

        TwitterPreprocessor twitterPreprocessor = new TwitterPreprocessor();

        from(queryQueue2)
                .autoStartup(false)
                .to("log:httpQuery?level=INFO&showHeaders=true")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader(Exchange.HTTP_QUERY, simple("q=${body}&lang=en&rpp=100"))
                .to("http://search.twitter.com/search.json")
                .convertBodyTo(String.class, "UTF-8")
                .process(twitterPreprocessor)
                .split().xpath("//twits/twit").streaming()
                .convertBodyTo(Twit.class)
                .to("log:QueryValue?level=INFO&showHeaders=true")
                .setHeader("CamelFileName").simple(UUID.randomUUID().toString())
                .to(outputDirectory);

    }

}
