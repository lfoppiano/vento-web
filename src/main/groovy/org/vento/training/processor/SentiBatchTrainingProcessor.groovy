package org.vento.training.processor

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.vento.gate.GateBatchProcessing
import org.vento.semantic.sentiment.SentiBatchProcessingImpl

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 24/12/12
 * Time: 23:53
 * To change this template use File | Settings | File Templates.
 */
class SentiBatchTrainingProcessor implements Processor{
    private GateBatchProcessing engine;
    private String corpusDirectory;

    //TODO: all paths from windows, have to be changed and put into config
    @Override
    public void process(Exchange exchange) throws Exception {
        engine.addAllToCorpus(new URL(corpusDirectory), "xml");
        engine.perform();
    }

    GateBatchProcessing getEngine() {
        return engine
    }

    void setEngine(GateBatchProcessing engine) {
        this.engine = engine
    }

    String getCorpusDirectory() {
        return corpusDirectory
    }

    void setCorpusDirectory(String corpusDirectory) {
        this.corpusDirectory = corpusDirectory
    }


}

