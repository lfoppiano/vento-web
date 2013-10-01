package org.vento.sentiment.evaluation

import com.mongodb.DBObject
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.vento.sentiment.classification.ClassificationWrapper

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 26/12/12
 * Time: 22:19
 * To change this template use File | Settings | File Templates.
 */
public class VentoCalculationProcessor implements Processor {

    ClassificationWrapper classificationWrapper

    private confusionMatrix = ['tp': 0, 'fp': 0, 'fn': 0, 'tn': 0]
    private observationMap = [:] //"1.0":confusionMatrix.clone(),"2.0":confusionMatrix.clone(),"3.0":confusionMatrix.clone()
    private domainClassLabels = '1.0,2.0'
    private totalPrecision = 0
    private totalRecall = 0

    @Override
    public void process(Exchange exchange) throws Exception {

        List<DBObject> twits = exchange.getIn().getBody()

        String predictedClass = ''
        String observedClass = ''

        domainClassLabels.split(',').each {
            observationMap.put(it, confusionMatrix.clone())
        }

        twits.each { element ->

            observedClass = element.get("score")
            predictedClass = classificationWrapper.classify(element.get("text"))

            println("Observed: ${observedClass} vs Predicted: ${predictedClass}")

            if (predictedClass.equals(observedClass)) {
                observationMap[predictedClass]['tp']++
                (observationMap.keySet() - predictedClass).each { observationMap[it]['tn']++ }
            } else {
                if (observationMap.keySet().contains(predictedClass))
                    observationMap[predictedClass]['fp']++
                else {
                    println "no class label found !!!"

                    observationMap[observedClass]['fn']++
                    (observationMap.keySet() - predictedClass - observedClass).each {
                        observationMap[it]['tn']++
                    }
                }
            }
        }

        observationMap.each { classLabel, observationMatrix ->

            def classPrecision = null
            def classRecall = null

            println "class: ${classLabel} observation matrix: ${observationMatrix}"

            def tpFp = observationMatrix['tp'] + observationMatrix['fp']
            def tpFn = observationMatrix['tp'] + observationMatrix['fn']

            if (tpFp != 0) {
                classPrecision = observationMatrix['tp'] / tpFp
                totalPrecision = +classPrecision
            }
            if (tpFn != 0) {
                classRecall = observationMatrix['tp'] / tpFn
                totalRecall = +classRecall
            }

            println "\n class: ${classLabel}, precision = ${classPrecision}"
            println "\n class: ${classLabel}, recall = ${classRecall}"
        }

        //println "\nprecision: "+totalPrecision/3
        //println "recall: "+totalRecall/3+"\n"
    }


    public void setClassificationWrapper(ClassificationWrapper classifier) {
        this.classificationWrapper = classifier;
    }
}