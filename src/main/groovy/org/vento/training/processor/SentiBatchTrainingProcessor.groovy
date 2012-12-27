package org.vento.training.processor

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.vento.sentiment.gate.GateBatchProcessing
import org.vento.sentiment.gate.SentiBatchProcessingImpl

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 24/12/12
 * Time: 23:53
 * To change this template use File | Settings | File Templates.
 */
class SentiBatchTrainingProcessor implements Processor{
    private GateBatchProcessing engine;

    private String gateHome;
    private String projectConfigFile;
    private String dataStoreDir;
    private String corpusName;
    private String corpusDirectory;

    @Override
    public void process(Exchange exchange) throws Exception {
        String dataStoreDir = "file:/tmp/twitter/tempTrainingStore";

        engine = new SentiBatchProcessingImpl(
                new File(gateHome),
                new File(projectConfigFile),
                this.dataStoreDir,
                corpusName);

        engine.addAllToCorpus(new URL("file:/tmp/twitter/training"), "xml");
        engine.perform();
    }

    void setEngine(GateBatchProcessing engine) {
        this.engine = engine
    }

    void setGateHome(String gateHome) {
        this.gateHome = gateHome
    }

    void setProjectConfigFile(String projectConfigFile) {
        this.projectConfigFile = projectConfigFile
    }

    void setDataStoreDir(String dataStoreDir) {
        this.dataStoreDir = dataStoreDir
    }

    void setCorpusName(String corpusName) {
        this.corpusName = corpusName
    }

    void setCorpusDirectory(String corpusDirectory) {
        this.corpusDirectory = corpusDirectory
    }
}

