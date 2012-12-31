package org.vento.crawler.twitter.processor

import groovy.json.JsonSlurper
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.vento.model.ScoreHistory
import org.vento.model.Twit
import org.vento.model.Twits
import org.vento.crawler.amazon.utility.StringProcessor
import org.vento.utility.VentoTypes

public class TwitterPreprocessor implements Processor {
    public void process(Exchange exchange) throws Exception {

        def json = exchange.getIn().getBody(String)
        def resultJson = new JsonSlurper().parseText(json)

        def root = new Twits()

        root.twits = new ArrayList<Twit>();

        resultJson.results.each{ result ->
            def twit = new Twit()
            twit.twitterId = result.id_str
            twit.query = resultJson.query
            twit.text = textProcessing(result.text)
            twit.createdAt = result.created_at
            twit.fromUserIdStr = result.from_user_id_str
            twit.toUserIdStr = result.to_user_id_str
            twit.fromUser = result.from_user
            twit.source = result.source
            twit.isoLanguageCode = result.iso_language_code
            twit.geo =  result.geo
            twit.type = VentoTypes.CLASSIFICATION
            twit.getScoreHistories()
            root.twits.add(twit)
        }
        exchange.getOut().setBody(root)
    }

    private String textProcessing(String text) {
        //Remove recipients
        text = text.replaceAll(/@\w+/, '')
        //Remove tags
        text = text.replaceAll(/#\w+/, '')
        //Remove emoticons
        text = text.replaceAll(/[;:8](-)?[)(P]/, '')
        //Double space to space
        text = text.replaceAll(/\s\s/, ' ')
        //Remove links/urls
        text = text.replaceAll(/http:\/\/[a-zA-Z0-9\/-=.:]+/, '')
        //Remove retweets
        text = text.replaceAll(/^?RT\s?:/, '')
        //Remove invalid characters
        text = StringProcessor.removeInvalidUtf8Chars(text)

        text = text.trim()

        return text
    }

}
