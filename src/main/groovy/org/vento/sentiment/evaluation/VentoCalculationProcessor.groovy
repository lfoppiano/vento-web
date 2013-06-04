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

    private observationMatrix = ['tp':0,'fp':0,'fn':0,'tn':0]
    private classValues = ["1.0":observationMatrix.clone(),"2.0":observationMatrix.clone(),"3.0":observationMatrix.clone()]
    private totalPrecision = 0
    private totalRecall = 0

    @Override
    public void process(Exchange exchange) throws Exception {

        List<DBObject> twits = exchange.getIn().getBody()
        String result = ''

        twits.each{element->

            result = classificationWrapper.classify(element.get("text"))

            if (result.equals(element.get("score"))){
                classValues[result]['tp']++
                (classValues.keySet() - result).each {classValues[it]['tn']++}
            }
            else
            {
                if (classValues.keySet().contains(result)) classValues[result]['fp']++
                classValues[element.get("score")]['fn']++
            }
        }

        classValues.each {classLabel,observationMatrix->

            def tpFp = observationMatrix['tp']+observationMatrix['fp']
            def tpFn = observationMatrix['tp']+observationMatrix['fn']

            if (tpFp!=0) totalPrecision=+observationMatrix['tp']/tpFp
            if (tpFn!=0) totalRecall=+observationMatrix['tp']/tpFn

        }

        println("\nprecision: "+totalPrecision/3)
        println("recall: "+totalRecall/3+"\n")
    }


    public void setClassificationWrapper(ClassificationWrapper classifier) {
        this.classificationWrapper = classifier;
    }
}
