package org.vento.crawler.route;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;
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

    @EndpointInject(ref = "outputDirectory")
    private Endpoint outputDirectory;

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
                .convertBodyTo(Twit.class)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Message exchangeIn = exchange.getIn();
                        exchangeIn.setHeader("twitterId", ((Twit) exchangeIn.getBody()).getTwitterId());
                    }
                })
                .to("direct:flight");

        //.convertBodyTo(String.class)
        //.to("log:QueryValue?level=INFO&showHeaders=true")
        //.processRef("gateClassifierProcessor")
        //.processRef("simpleClassifierProcessor")
        /*.process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Twit bodyIn = (Twit) exchange.getIn().getBody();

                DBObject dbObject = new BasicDBObject();
                dbObject.put("_id", bodyIn.getId());
                dbObject.put("text", bodyIn.getText());

                exchange.getIn().setBody(dbObject);
            }
        })*/
        from("direct:flight")
                .idempotentConsumer(header("twitterId"), MemoryIdempotentRepository.memoryIdempotentRepository(1000000))
                        //.idempotentConsumer(header("twitterId"), FileIdempotentRepository.fileIdempotentRepository(new File("file:///tmp/twitter/idempotent")))
                .setHeader("CamelFileName").simple(UUID.randomUUID().toString())
                .to(outputDirectory);

        from("file:src/data/in?fileName=mongoQuery.txt&noop=true&idempotent=false&delay=6000")
                .autoStartup(true)
                .setHeader(MongoDbConstants.LIMIT, constant(500))
                .to("mongodb:mongoDb?database=vento&collection=reports&operation=findAll")
                .split(body())
                .processRef("gateClassifierProcessor")
                .log("${body}")
                .log("${body.get(\"twitterId\")}")
                .log("${body.get(\"text\")}")
                .log("${body.get(\"score\")}");

    }

}
