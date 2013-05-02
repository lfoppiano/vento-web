package org.vento.crawler.twitter.processor

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.vento.model.Twit
import org.vento.utility.VentoTypes

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 01/02/13
 * Time: 20:08
 * To change this template use File | Settings | File Templates.
 */
class ImporterPostProcessor implements Processor {
    public void process(Exchange exchange) throws Exception {

        List<String> tweet = (List<String>) exchange.getIn().getBody()[0]

        Twit t = new Twit();
//            t.twitterId = ""+tweet.id
//            t.query = exchange.getIn().getHeader("query")
        t.text = tweet[1]
//            t.createdAt = tweet.getCreatedAt()
//            t.fromUser = tweet.getUser()
//            t.source = tweet.getSource()
//            t.geo = tweet.getGeoLocation()
        t.score = mapScoreValue(tweet[0])
        t.referenceScore = t.score
        t.type = VentoTypes.TRAINING
//            t = TwitHelper.analyzeAndCleanEmotions(t);
        //t.setText(StringProcessor.textProcessing(t.getText()));

//            ts.getTwits().add(t);


        exchange.getOut().setBody(t)

    }

    private String mapScoreValue(String score) {
        score = (score.equals("0"))? 1 : 3
        return score + ".0"
    }
}
