package org.vento.crawler.amazon.processor

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.vento.model.Twit
import org.vento.model.Twits
import org.vento.utility.StringProcessor

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

                review.text = StringProcessor.removeInvalidUtf8Chars(reviewText)
                //review.text = reviewText
                review.score = score

                if (['1.0', '2.0'].contains(review.score)) {
                    review.score = '1.0'
                }else if(['4.0', '5.0'].contains(review.score)) {
                    review.score = '3.0'
                }else if(review.score == '3.0'){
                    review.score = '2.0'
                }

                //println "[${review.score}] ${review.text}"
                reviews.twits.add(review)

                score = ""
                reviewText = ""
            }
        }

        exchange.getOut().setBody(reviews)
    }


}
