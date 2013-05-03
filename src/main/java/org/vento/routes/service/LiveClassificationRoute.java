package org.vento.routes.service;

import groovy.json.JsonBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.twitter.TwitterConstants;
import org.vento.model.Twit;
import org.vento.model.Twits;
import twitter4j.Status;

import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 13/01/13
 * Time: 21:56
 * To change this template use File | Settings | File Templates.
 */
public class LiveClassificationRoute extends RouteBuilder {

    public void configure() {

        from("jetty:http://0.0.0.0:8889/classification")
                .routeId("Live classification route")
                .autoStartup(true)
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
                .setHeader(TwitterConstants.TWITTER_COUNT, constant(20))
                .setHeader(TwitterConstants.TWITTER_NUMBER_OF_PAGES, constant(1))
                .to("twitter://search?" +
                        "filterOld=false&" +
                        "consumerKey=bjGMxAJIv2uc10ESDUx6w&" +
                        "consumerSecret=5bmm77bQnD4YbRXUnYv36AteUAcK1Cy6MqMGCpqXY&" +
                        "accessToken=1087160808-gqOvlbHzAIDx8vGwdipaFuhUDlGhXWNwuYVjYt9&" +
                        "accessTokenSecret=sgjtvpe7akX1GsRSsK9RR4VvLSO2UUAGrMwtMgRN0")
                .processRef("twitterLiveClassificationProcessor");
                /*.process(new Processor() {

                    @Override
                    public void process(Exchange exchange) throws Exception {
                        List list = (List) exchange.getIn().getBody();
                        Iterator i = list.iterator();

                        Twits ts = new Twits();
                        while (i.hasNext()) {
                            Status tweet = (Status) i.next();
                            Twit t = new Twit();
                            t.setTwitterId("" + tweet.getId());
                            t.setText(tweet.getText());
                            //t = TwitHelper.analyzeAndCleanEmotions(t);
                            //t.setText(StringProcessor.textProcessing(tweet.getText()));
                            ts.getTwits().add(t);
                        }

                        JsonBuilder jsonBuilder = new groovy.json.JsonBuilder(ts);
                        exchange.getOut().setBody(jsonBuilder.toString());
                        exchange.getOut().setHeader("Access-Control-Allow-Origin", "*");
                    }
                });*/
    }
}
