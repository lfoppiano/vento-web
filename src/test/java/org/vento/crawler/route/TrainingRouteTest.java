package org.vento.crawler.route;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.vento.routes.sentiment.TrainingRoute;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 22/12/12
 * Time: 10:29
 * To change this template use File | Settings | File Templates.
 */
public class TrainingRouteTest extends CamelTestSupport {

    /*@Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @EndpointInject(uri= "mock:queue")
    protected MockEndpoint queryQueue;
    */
    private AbstractApplicationContext applicationContext;
    private ProducerTemplate template;

    @Before
    public void setUp() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext("test-camel-context.xml");
        CamelContext camelContext = applicationContext.getBean("camel-1", CamelContext.class);
        template = camelContext.createProducerTemplate();

        camelContext.start();
    }

    @Ignore
    @Test
    public void testMongoDbHeaderParameters() throws Exception{
        MockEndpoint mongoConnector = applicationContext.getBean("mongoConnector", MockEndpoint.class);
        mongoConnector.expectedHeaderReceived(MongoDbConstants.LIMIT, 500);

        template.sendBody("direct:flight", "fake input");

        mongoConnector.assertIsSatisfied();


    }

    @Ignore("Doesn't work still")
    @Test
    public void testMongoResult() throws Exception {
        MockEndpoint trainingTemp = applicationContext.getBean("outputReviewsDirectory", MockEndpoint.class);
        trainingTemp.expectedMessageCount(2);

        List<DBObject> db = new ArrayList<DBObject>();
        DBObject db1 = new BasicDBObject();
        db1.put("text", "fake test");
        db1.put("score", "fake score");
        db.add(db1);

        DBObject db2 = new BasicDBObject();
        db2.put("text", "fake test");
        db2.put("score", "fake score");
        db.add(db2);

        template.sendBody("mock:mongo", db);

        trainingTemp.assertIsSatisfied();
    }



    @After
    public void tearDown() throws Exception {
        if (applicationContext != null) {
            applicationContext.stop();
        }

    }

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new TrainingRoute();
    }
}
