package org.vento.routes.crawler.twitter;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.vento.crawler.twitter.processor.ImporterPostProcessor;


/**
 * Created by IntelliJ IDEA.
 * User: lfoppiano
 * Date: 2/10/12
 * Time: 4:57 PM
 *
 * The format of the CSV has to be:
 *  1) one tweet per line
 *  2) first element is the classification value (1 = positive, 0 = negative)
 *  3) second element is the text
 */

public class TwitterDatasetImporterRoute extends RouteBuilder {

    @EndpointInject(ref = "sourceFileImporter")
    private Endpoint sourceFileImporter;
    @EndpointInject(ref = "mongoStorageSaveImport")
    private Endpoint dataStorage;
    @EndpointInject(ref = "rejectTwitterLocation")
    private Endpoint twitterRejectEndpoint;

    public void configure() {

        errorHandler(
                deadLetterChannel(twitterRejectEndpoint)
                        .retryAttemptedLogLevel(LoggingLevel.WARN)
        );

        from(sourceFileImporter)
                .routeId("Twitter CSV importer")
                .split().tokenize("\n")
                //.to("seda:importer")
                //.unmarshal().csv()
                //.to("mock:some")
                .process(new ImporterPostProcessor())
                .to(dataStorage);


        /*from("seda:queryQueue?concurrentConsumers=1")
                .routeId("Twitter CSV importer 2")

                .convertBodyTo(Twit.class)
                .to(dataStorage);*/
    }

}
