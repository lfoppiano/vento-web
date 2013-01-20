package org.vento.utility

import org.vento.model.Twit;

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 19/01/13
 * Time: 16:15
 * To change this template use File | Settings | File Templates.
 */
public class TwitHelper {

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
    public static Twit analyzeAndCleanEmotions(Twit body){
        //String positiveRegex =  /(([;:8][-=]))[P)}DFf]/
        String positiveRegex =  /([;:8]|[-=]|[;:8][-=])[P)}DFf]/
        String negativeRegex =  /([;:8]|[-=]|[;:8][-=])[({]/

        def text = body?.text
        def referenceScore = body.referenceScore
        text = text.replaceAll(positiveRegex) {
            //println it
            if(!referenceScore)
                referenceScore = '3.0'

            return ''
        }

        text = text.replaceAll(negativeRegex) {
            //println it
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


}
