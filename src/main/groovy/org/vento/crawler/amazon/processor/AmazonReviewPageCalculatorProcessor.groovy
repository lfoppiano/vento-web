package org.vento.crawler.amazon.processor;


import org.apache.camel.Exchange
import org.apache.camel.Processor

/**
 * Created by IntelliJ IDEA.
 * User: lfoppiano
 * Date: 3/17/12
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class AmazonReviewPageCalculatorProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        String body = (String) exchange.getIn().getBody();
        def maxPageNumber

        body.tokenize('\n').eachWithIndex { line, index ->

            line.findAll(/&hellip; <a href=".*pageNumber=\d{1,}" >(\d{1,})<\/a>| <a/){ groups ->

                if(groups[1]) {
                    maxPageNumber = groups[1]
                }
            }

        }

        if(maxPageNumber)
            exchange.getIn().setHeader("maxPageNumber", maxPageNumber)
        else
            exchange.getIn().setHeader("maxPageNumber", '10')

    }
}
