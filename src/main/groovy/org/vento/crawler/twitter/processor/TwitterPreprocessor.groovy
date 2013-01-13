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
            twit = analyzeAndCleanEmotions(twit)
            twit.text = textProcessing(twit.text)
            root.twits.add(twit)
        }


        exchange.getOut().setBody(root)
    }

    /**
     * Analyze and remove emoticons.
     *  - analyze the positive emoticons, and set referenceScore = 3.0 if any is found
     *  - analyze the negative emoticons,
     *      * if no referenceScore has been set, it set one
     *      * if a positive reference score has been set, it set to 0.0 the result which
     *          invalidate the calculation, because there is no clear state of emotion
     *
     * @param body a twit with at least text != null
     * @return a twit with the text cleaned and the referenceScore set
     */
    private analyzeAndCleanEmotions(Twit body){
        String positiveRegex =  /([;:8]?[-=]?)[P)}DFf]/
        String negativeRegex =  /[;:8]?[-=]?[(P){]/

        def text = body?.text
        def referenceScore = body.referenceScore
        text = text.replaceAll(positiveRegex) {
            if(!referenceScore)
                referenceScore = '3.0'

            return ''
        }

        text = text.replaceAll(negativeRegex) {
            if(!referenceScore)
                referenceScore = '1.0'
            else if(referenceScore == '3.0')
                referenceScore = '0.0'

            return ''
        }

        body.text = text.trim()
        body.referenceScore = referenceScore == '0.0' ? null : referenceScore
        return body;
    }

    private String textProcessing(String text) {
        //Remove recipients
        text = text.replaceAll(/@\w+(:)?/, '')

        //Remove hash of tags
        text = text.replaceAll(/#/, '')

        //Remove emoticons neutral
        //text = text.replaceAll(/[;:8](-)?[(P)}{D]/, '')

        //Double space to space
        text = text.replaceAll(/\s\s/, ' ')

        //Remove links/urls
        text = text.replaceAll(/(via)? http:\/\/[a-zA-Z0-9\/-=.:]+/, '')

        //Remove retweets
        text = text.replaceAll(/^?RT\s?:/, '')

        //Remove invalid characters
        text = StringProcessor.removeInvalidUtf8Chars(text)

        text = text.trim()

        return text
    }

}
