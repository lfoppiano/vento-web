package org.vento.crawler.twitter.processor

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.vento.model.Twit
import org.vento.model.Twits
import org.vento.utility.StringProcessor
import org.vento.utility.TwitHelper
import org.vento.utility.VentoTypes
import twitter4j.Status

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 01/02/13
 * Time: 20:08
 * To change this template use File | Settings | File Templates.
 */
class Twitter4jPreprocessor implements Processor{
    public void process(Exchange exchange) throws Exception {

        List<Status> tweets = (List<Status>) exchange.getIn().getBody()

        Iterator i = tweets.iterator();

        Twits ts = new Twits();
        while (i.hasNext()) {
            Status tweet = (Status) i.next();
            Twit t = new Twit();
            t.twitterId = ""+tweet.id
            t.query = exchange.getIn().getHeader("query")
            t.text = tweet.text
            t.createdAt = tweet.getCreatedAt()
            t.fromUser = tweet.getUser()
            t.source = tweet.getSource()
            t.geo = tweet.getGeoLocation()

            t.type = VentoTypes.CLASSIFICATION
            t = TwitHelper.analyzeAndCleanEmotions(t);
            t.setText(StringProcessor.textProcessing(tweet.getText()));

            ts.getTwits().add(t);
        }

        exchange.getOut().setBody(ts)
    }
}
