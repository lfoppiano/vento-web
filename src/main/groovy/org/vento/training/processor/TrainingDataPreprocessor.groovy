package org.vento.training.processor

import com.mongodb.DBObject
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.vento.model.Twit

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 24/12/12
 * Time: 15:34
 * To change this template use File | Settings | File Templates.
 */
class TrainingDataPreprocessor implements Processor{

    @Override
    public void process(Exchange exchange) throws Exception {
        Twit twit = new Twit();
        DBObject inBody = (DBObject) exchange.getIn().getBody();

        twit.setText((String) inBody.get("text"));
        String twitterId = (String) inBody.get("twitterId")
        twit.setTwitterId(twitterId);
        twit.setScore((String) inBody.get("score"));
        exchange.getIn().setBody(twit);
        exchange.getIn().setHeader('twitterId', twitterId)
    }
}
