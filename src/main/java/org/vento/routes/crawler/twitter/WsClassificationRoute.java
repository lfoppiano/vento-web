package org.vento.routes.crawler.twitter;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.twitter.TwitterConstants;

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 13/01/13
 * Time: 21:56
 * To change this template use File | Settings | File Templates.
 */
public class WsClassificationRoute extends RouteBuilder {

    public void configure() {

        from("jetty:http://0.0.0.0:8888/classification")
                .routeId("Web service classification route")
                        //.to("log:query?level=INFO&showAll=true")
                /*.process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        // just get the body as a string
                        String provider = (String) exchange.getIn().getHeader("provider");
                        String searchTerm = (String) exchange.getIn().getHeader("search");
                    }
                })*/
                .setHeader(TwitterConstants.TWITTER_SEARCH_LANGUAGE, header("lang"))
                .setHeader(TwitterConstants.TWITTER_KEYWORDS, header("search"))
                .setHeader(TwitterConstants.TWITTER_COUNT, constant(100))
                .setHeader(TwitterConstants.TWITTER_NUMBER_OF_PAGES, constant(5))
                .to("twitter://search?" +
                        "filterOld=false&" +
                        "consumerKey=bjGMxAJIv2uc10ESDUx6w&" +
                        "consumerSecret=5bmm77bQnD4YbRXUnYv36AteUAcK1Cy6MqMGCpqXY&" +
                        "accessToken=1087160808-gqOvlbHzAIDx8vGwdipaFuhUDlGhXWNwuYVjYt9&" +
                        "accessTokenSecret=sgjtvpe7akX1GsRSsK9RR4VvLSO2UUAGrMwtMgRN0")

                .to("log:twitter?level=INFO&showAll=true")
                .processRef("twitterLiveClassificationProcessor");


        /*from("twitter://search?type=direct&keywords=camel&"+
                "consumerKey=bjGMxAJIv2uc10ESDUx6w&" +
                "consumerSecret=5bmm77bQnD4YbRXUnYv36AteUAcK1Cy6MqMGCpqXY&" +
                "accessToken=1087160808-gqOvlbHzAIDx8vGwdipaFuhUDlGhXWNwuYVjYt9&" +
                "accessTokenSecret=sgjtvpe7akX1GsRSsK9RR4VvLSO2UUAGrMwtMgRN0")
                .convertBodyTo(String.class)
                .to("mock:baogigi");

        from("twitter://search?type=direct&keywords=camel&lang=it&count=100&"+
                "consumerKey=bjGMxAJIv2uc10ESDUx6w&" +
                "consumerSecret=5bmm77bQnD4YbRXUnYv36AteUAcK1Cy6MqMGCpqXY&" +
                "accessToken=1087160808-gqOvlbHzAIDx8vGwdipaFuhUDlGhXWNwuYVjYt9&" +
                "accessTokenSecret=sgjtvpe7akX1GsRSsK9RR4VvLSO2UUAGrMwtMgRN0")
                .convertBodyTo(String.class)
                .to("mock:baogigi1");

        from("twitter://search?type=direct&keywords=camel&lang=es&count=1&numberOfPages=2&"+
                "consumerKey=bjGMxAJIv2uc10ESDUx6w&" +
                "consumerSecret=5bmm77bQnD4YbRXUnYv36AteUAcK1Cy6MqMGCpqXY&" +
                "accessToken=1087160808-gqOvlbHzAIDx8vGwdipaFuhUDlGhXWNwuYVjYt9&" +
                "accessTokenSecret=sgjtvpe7akX1GsRSsK9RR4VvLSO2UUAGrMwtMgRN0")
                .convertBodyTo(String.class)
                .to("mock:baogigi2");*/

    }
}
