package org.vento.sentiment.classification

import org.apache.camel.Processor
import org.apache.camel.Exchange
import org.vento.model.Twit
import org.vento.sentiment.SimpleBatchClassification

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 27/07/12
 * Time: 10.19
 * To change this template use File | Settings | File Templates.
 */
class SentiBatchClassificationSimpleProcessor implements Processor {

    SimpleBatchClassification classifier

    @Override
    void process(Exchange exchange) {
        String input = (String) exchange.getIn().getBody();
        /*
        BufferedWriter out = null;
        File temp = null;

        try {
            // Create temp file.
            temp = File.createTempFile("gate-classifier", ".xml");

            // Delete temp file when program exits.
            temp.deleteOnExit();

            // Write to temp file
            out = new BufferedWriter(new FileWriter(temp));
            out.write(input);

        } catch (IOException e) {
            System.out.println("Ma che cazzo!");
        } finally {
            out.close();
        }
*/
        Double result = classifier.simpleClassify(input);

        Twit twit = exchange.getIn().getBody(Twit.class);

        twit.setReferenceScore(result.toString());

        exchange.getIn().setBody(twit);

    }
}
