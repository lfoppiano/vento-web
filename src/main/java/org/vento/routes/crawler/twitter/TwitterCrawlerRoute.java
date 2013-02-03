package org.vento.routes.crawler.twitter;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.vento.model.Twit;
import org.vento.model.Twits;

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

    @EndpointInject(ref = "mongoStorageSave")
    private Endpoint dataStorage;

    @EndpointInject(ref = "rejectTwitterLocation")
    private Endpoint twitterRejectEndpoint;

    public void configure() {

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

        from(sourceFileQuery)
                .routeId("Twitter url builder")
                .split().tokenize("\n")
                .loop(15).copy()
                .transform(body().append(simple("&page=${header.CamelLoopIndex}++")))
                .to("seda:queryQueue");

        from("seda:queryQueue?concurrentConsumers=1")
                .routeId("Twitter crawler")
                        //.to("log:httpQuery?level=INFO&showHeaders=true")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader(Exchange.HTTP_QUERY, simple("q=${body}&lang=en&rpp=100"))
                .to("http://search.twitter.com/search.json?httpClient.cookiePolicy=ignoreCookies")
                .convertBodyTo(String.class, "UTF-8")
                //.to("mock:output")
                .processRef("twitterPreprocessor")
                .convertBodyTo(Twits.class)
                .split().xpath("//twits/twit").streaming()
                .convertBodyTo(Twit.class)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Message exchangeIn = exchange.getIn();
                        exchangeIn.setHeader("twitterId", ((Twit) exchangeIn.getBody()).getTwitterId());
                    }
                })
                .idempotentConsumer(header("twitterId"), MemoryIdempotentRepository.memoryIdempotentRepository(1000000))
                        //.idempotentConsumer(header("twitterId"), FileIdempotentRepository.fileIdempotentRepository(new File("file:///tmp/twitter/idempotent")))
                .setHeader("CamelFileName").simple(UUID.randomUUID().toString())
                .to(dataStorage);

    }

}
