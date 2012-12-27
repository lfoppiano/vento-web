package org.vento.sentiment.training.processor

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.commons.io.FileUtils
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

    private File gateHome;
    private File projectConfigFile;
    private String dataStoreDir;
    private String corpusName;
    private URL corpusDirectory;

    @Override
    public void process(Exchange exchange) throws Exception {
        //String dataStoreDir = "file:/tmp/twitter/tempTrainingStore";
        //def corpusDirectory = new URL("file:/tmp/twitter/training")

        engine = new SentiBatchProcessingImpl(
                gateHome,
                projectConfigFile,
                this.dataStoreDir,
                corpusName);

        engine.addAllToCorpus(corpusDirectory, "xml");
        engine.perform();

        FileUtils.cleanDirectory(new File(corpusDirectory.toURI()))
    }

    void setEngine(GateBatchProcessing engine) {
        this.engine = engine
    }

    void setGateHome(File gateHome) {
        this.gateHome = gateHome
    }

    void setProjectConfigFile(File projectConfigFile) {
        this.projectConfigFile = projectConfigFile
    }

    void setDataStoreDir(String dataStoreDir) {
        this.dataStoreDir = dataStoreDir
    }

    void setCorpusName(String corpusName) {
        this.corpusName = corpusName
    }

    void setCorpusDirectory(URL corpusDirectory) {
        this.corpusDirectory = corpusDirectory
    }
}

