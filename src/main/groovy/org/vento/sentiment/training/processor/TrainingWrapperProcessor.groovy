package org.vento.sentiment.training.processor

import gate.Corpus
import gate.CorpusController
import gate.Factory
import gate.util.ExtensionFileFilter
import org.apache.camel.Exchange
import org.apache.camel.Processor

/**
 * Created with IntelliJ IDEA.
 * User: Martin
 * Date: 11/05/2013
 * Time: 17:42
 * To change this template use File | Settings | File Templates.
 */
class TrainingWrapperProcessor implements Processor{

    private CorpusController trainer
    private String corpusDirectory

    @Override
    public void process(Exchange exchange){

        Corpus tmpCorpus = Factory.newCorpus()
        tmpCorpus.populate(new URL(corpusDirectory), new ExtensionFileFilter("XML files", "xml"), "UTF-8", true)
        trainer.setCorpus(tmpCorpus)
        trainer.execute()
    }


    public void setTrainer(CorpusController trainer) {
        this.trainer = trainer;
    }

    public void setCorpusDirectory(String directory) {
        this.corpusDirectory = directory;
    }
}
