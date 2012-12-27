package org.vento.routes.crawler.amazon;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.vento.crawler.amazon.AmazonReviewAggregationStrategy;
import org.vento.crawler.amazon.processor.AmazonReviewPageCalculatorProcessor;
import org.vento.crawler.amazon.processor.AmazonReviewProcessor;
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
public class ReviewCrawlerRoute extends RouteBuilder {

    @EndpointInject(ref = "outputReviewsDirectory")
    private Endpoint outputReviewsDirectory;

    @EndpointInject(ref = "sourceFileReviews")
    private Endpoint sourceFileReview;

    @EndpointInject(ref = "rejectReviewsLocation")
    private Endpoint rejectEndpoint;


    public void configure(){

        AmazonReviewProcessor amazonReviewProcessor = new AmazonReviewProcessor();
        AmazonReviewPageCalculatorProcessor amazonReviewPageCalculatorProcessor = new AmazonReviewPageCalculatorProcessor();
        AmazonReviewAggregationStrategy amazonReviewAggregationStrategy = new AmazonReviewAggregationStrategy();

        errorHandler(deadLetterChannel(rejectEndpoint).useOriginalMessage());

        onException(java.net.SocketException.class).maximumRedeliveries(5).redeliveryDelay(3000);

        from(sourceFileReview)
                .routeId("ReviewsUrlPreparation")
                .split().tokenize("\n")
                .enrich("direct:enrich", amazonReviewAggregationStrategy)
                .log("Product: ${body} - maxPageNumber: ${header.maxPageNumber} ")
                .loop(simple("${header.maxPageNumber}")).copy()
                .transform(body().append(simple("?pageNumber=${header.CamelLoopIndex}++")))
                .to("seda:review");

        from("direct:enrich")
                .routeId("ReviewsPageCalculation")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader(Exchange.HTTP_PATH, body())
                .to("http://www.amazon.com")
                .convertBodyTo(String.class)
                .process(amazonReviewPageCalculatorProcessor);

        from("seda:review?concurrentConsumers=21")
                .routeId("ReviewsCrawler")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader(Exchange.HTTP_PATH, body())
                .to("http://www.amazon.com/")
                .convertBodyTo(String.class, "UTF-8")
                .process(amazonReviewProcessor)
                .convertBodyTo(Twits.class, "UTF-8")
                .split().xpath("//twits/twit").streaming()
                .convertBodyTo(Twit.class)
                .setHeader("CamelFileName").simple(UUID.randomUUID().toString())
                .to(outputReviewsDirectory);

    }

}
