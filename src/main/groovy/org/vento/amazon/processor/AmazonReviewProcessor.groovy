package org.vento.amazon.processor

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.vento.model.Twit
import org.vento.model.Twits

/**
 * Created by IntelliJ IDEA.
 * User: lfoppiano
 * Date: 3/16/12
 * Time: 8:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class AmazonReviewProcessor implements Processor {

    void process(Exchange exchange) {

        def text = exchange.getIn().getBody();

        Twits reviews = new Twits()
        reviews.twits = []

        String score = ""
        String reviewText = ""

        text = text.tokenize('\n')

        text.eachWithIndex{ line, index ->
            if((!score) || (score && !reviewText)){
                line.find(/title="(\d\.\d) out of \d stars"/){ fm ->
                    score = fm[1]
                }

                line.find(/<b><span class=".*">This review is from:/){ fn ->
                    reviewText = text[index + 2]
                }
            }

            if(score && reviewText){
                Twit review = new Twit()
                reviewText = reviewText.replaceAll(/<br\s+\/>/, '')

                review.text = removeInvalidUtf8Chars(reviewText)
                //review.text = reviewText
                review.score = score

                //println "[${review.score}] ${review.text}"
                reviews.twits.add(review)

                score = ""
                reviewText = ""
            }
        }

        exchange.getOut().setBody(reviews)
    }

    private String removeInvalidUtf8Chars(String inString) {
        if (inString == null) return null;

        StringBuilder newString = new StringBuilder();
        char ch;

        for (int i = 0; i < inString.size(); i++){
            ch = inString[i];
            if ((ch < 0x00FD && ch > 0x001F) || ch == '\t' || ch == '\n' || ch == '\r') {
                newString.append(ch);
            }
        }
        return newString.toString();

    }
}
