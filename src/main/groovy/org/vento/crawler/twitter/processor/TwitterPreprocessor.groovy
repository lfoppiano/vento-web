package org.vento.crawler.twitter.processor

import groovy.json.JsonSlurper
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.vento.model.Twit
import org.vento.model.Twits
import org.vento.utility.StringProcessor
import org.vento.utility.TwitHelper
import org.vento.utility.VentoTypes

public class TwitterPreprocessor implements Processor {
    public void process(Exchange exchange) throws Exception {

        def json = exchange.getIn().getBody(String)
        def resultJson = new JsonSlurper().parseText(json)

        def root = new Twits()

        root.twits = new ArrayList<Twit>();

        resultJson.results.each{ result ->
            def twit = new Twit()

            //collecting
            twit.twitterId = result.id_str
            twit.query = resultJson.query
            twit.text = result.text
            twit.createdAt = result.created_at
            twit.fromUserIdStr = result.from_user_id_str
            twit.toUserIdStr = result.to_user_id_str
            twit.fromUser = result.from_user
            twit.source = result.source
            twit.isoLanguageCode = result.iso_language_code
            twit.geo =  result.geo
            twit.type = VentoTypes.CLASSIFICATION
            twit.getScoreHistories()

            //cleaning up and enriching
            twit = TwitHelper.analyzeAndCleanEmotions(twit)
            twit.text = StringProcessor.textProcessing(twit.text)
            root.twits.add(twit)
        }


        exchange.getOut().setBody(root)
    }

}
