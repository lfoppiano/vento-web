package org.vento.routes.crawler.amazon;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.vento.crawler.amazon.AmazonReviewAggregationStrategy;
import org.vento.crawler.amazon.processor.AmazonReviewPageCalculatorProcessor;
import org.vento.crawler.amazon.processor.AmazonReviewProcessor;
import org.vento.model.Twit;

import java.io.InputStream;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 * User: lfoppiano
 * Date: 3/15/12
 * Time: 9:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReviewCrawlerRouteTest extends CamelTestSupport {

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @Produce(uri = "direct:review")
    protected ProducerTemplate templateReview;

    @Produce(uri = "direct:error")
    protected ProducerTemplate templateError;

    @EndpointInject(uri= "mock:mockReject")
    protected MockEndpoint mockRejectEndpoint;

    @EndpointInject(uri= "mock:mock_splitted_input")
    protected MockEndpoint mock_splitted_input;

    @EndpointInject (uri="mock:mock_output")
    protected MockEndpoint mock_output;



    public void testMessageSplitAndQueryPreparation() throws Exception {

        InputStream sourceFileReview = this.getClass().getResourceAsStream("sourceFileReviews.txt");

        StringWriter writer = new StringWriter();
        IOUtils.copy(sourceFileReview, writer, "UTF-8");
        String sourceText = writer.toString();

        mock_splitted_input.expectedMessageCount(236);

        template.sendBody(sourceText);

        mock_splitted_input.assertIsSatisfied();

        Message message = mock_splitted_input.getExchanges().get(1).getIn();

        assertNotNull(message.getBody());
        assertNotNull(message.getHeaders());
        assertEquals("Leather-Stand-Apple-Black-Ipad2/product-reviews/B004T0EQP2?pageNumber=2", message.getBody());
        assertEquals("8", message.getHeader("maxPageNumber"));
    }

    /*   @Test
    public void testPageCountingAndMessagePreparation() throws Exception {

        InputStream responseExample = this.getClass().getResourceAsStream("example.response.amazon.review.html");

        StringWriter writer = new StringWriter();
        IOUtils.copy(responseExample, writer, "UTF-8");
        String response = writer.toString();

        mock_query_url.expectedMinimumMessageCount(75);
        mock_query_url.expectedMessageCount(75);

        template.sendBody(response);

        mock_query_url.assertIsSatisfied();



//        String lastMessage = mock_query_url.getExchanges().get(74).getIn().getBody().toString();
//        assertTrue(lastMessage.endsWith("&pageNumber=75"));

//        String expected = "Apple-MC979LL-Tablet-White-Generation/product-reviews/B0047DVWLW/ref=cm_cr_pr_top_link_74?ie=UTF8&showViewpoints=0&pageNumber=75";
//        assertEquals(expected, lastMessage);
    }*/


    public void testReviewExtraction() throws Exception {

        InputStream responseExample = this.getClass().getResourceAsStream("example.response.amazon.review.html");

        StringWriter writer = new StringWriter();
        IOUtils.copy(responseExample, writer, "UTF-8");
        String response = writer.toString();

        templateReview.sendBody(response);

        //9 reviews (+1 that is not recognized as known bug)
        assertEquals(9, mock_output.getExchanges().size());


        Twit message = (Twit) mock_output.getExchanges().get(0).getIn().getBody();
        assertEquals("3.0", message.getScore());

        String expectedText = "The new iPad 2 has become an invaluable extension of my office.  It's fast, has a great screen, and is easy to use.  With apps, I can rmeotely access my work desktop; access my company's entire lan, have all my files avialble to me onthe road via dropgox, have all my important Word, PDF, Excel, and Powerpoint files with me, call others using Yahoo, email on the fly, watch movies, listen to Spotify/iTunes, create a MindMap, create a presentation, take a picture and annotate it, annotate PDFs, edit Word, Excel files, find my way on a map, keep up with my blog, keep up and trade stocks, do unit conversions, request AAA roadside assistance, scan prices making drawings, and play games.  What is not to like!  IF you don't have one or you are skeptical, you have no idea what you're missing.  And more memory is better.  Also, having 3G is great for when you decide to turn it on while travelling.  Don't wait, just buy it now!";
        message = (Twit) mock_output.getExchanges().get(3).getIn().getBody();
        assertEquals(expectedText, message.getText());

    }


    public void testInvalidCharTreatment() throws Exception {

        InputStream errorExample = this.getClass().getResourceAsStream("amazon.response.invalid.char.html");
        InputStream correctExample = this.getClass().getResourceAsStream("amazon.response.valid.char.html");

        StringWriter writer = new StringWriter();
        IOUtils.copy(errorExample, writer, "UTF-8");
        String error = writer.toString();

        writer = new StringWriter();
        IOUtils.copy(correctExample, writer, "UTF-8");
        String correct= writer.toString();
        
        assertFalse(correct.equals(error) );

        mockRejectEndpoint.expectedMessageCount(1);
        mock_output.expectedMessageCount(10);

        templateReview.sendBody(error);
        templateReview.sendBody(correct);


        mockRejectEndpoint.assertIsSatisfied();
        mock_output.assertIsSatisfied();
    }

    @Test
    public void testErrorHandling() throws Exception {

        InputStream errorExample = this.getClass().getResourceAsStream("example.postprocessed.with.invalid.char.xml");
        InputStream correctExample = this.getClass().getResourceAsStream("example.postprocessed.with.valid.char.xml");

        StringWriter writer = new StringWriter();
        IOUtils.copy(errorExample, writer, "UTF-8");
        String error = writer.toString();

        writer = new StringWriter();
        IOUtils.copy(correctExample, writer, "UTF-8");
        String correct = writer.toString();

        mockRejectEndpoint.expectedMessageCount(1);
        mock_output.expectedMessageCount(20);
        templateError.sendBody(error);
        templateError.sendBody(correct);
        templateError.sendBody(correct);

        mockRejectEndpoint.assertIsSatisfied();
        mock_output.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder(){

            AmazonReviewProcessor amazonReviewProcessor = new AmazonReviewProcessor();
            AmazonReviewPageCalculatorProcessor amazonReviewPageCalculatorProcessor = new AmazonReviewPageCalculatorProcessor();
            AmazonReviewAggregationStrategy amazonReviewAggregationStrategy = new AmazonReviewAggregationStrategy();

            @Override
            public void configure() throws Exception {

                errorHandler(
                        deadLetterChannel(mockRejectEndpoint)
                                .useOriginalMessage()
                                .maximumRedeliveries(0)
                                .retryAttemptedLogLevel(LoggingLevel.WARN)
                );

                //onException(ClassCastException.class).logStackTrace(true).continued(true);

                from("direct:start")
                        .split().tokenize("\n")
                        .enrich("direct:enrich", amazonReviewAggregationStrategy)
                        .log("Body: ${body} - maxPageNumber: ${header.maxPageNumber} ")
                        .loop(simple("${header.maxPageNumber}")).copy()
                        .transform(body().append(simple("?pageNumber=${header.CamelLoopIndex}++")))
                        .to(mock_splitted_input);

                from("direct:enrich")
                        .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                        .setHeader(Exchange.HTTP_PATH, body())
                        .to("http://www.amazon.com")       //TODO: mock it
                        .convertBodyTo(String.class)
                        .process(amazonReviewPageCalculatorProcessor);

                from("direct:review")
                        .handleFault()
                        .convertBodyTo(String.class, "UTF-8")
                        .process(amazonReviewProcessor)
                        //.to("log:com.mycompany.order?level=INFO&showAll=true&multiline=true")
                        .split().xpath("//twits/twit").streaming()
                        .convertBodyTo(Twit.class)
                        .to(mock_output);

                from("direct:error")
                        .convertBodyTo(String.class, "UTF-8")
                        .split().xpath("//twits/twit").streaming()
                        .convertBodyTo(Twit.class)
                        .to(mock_output);
            }
        };
    }
}
