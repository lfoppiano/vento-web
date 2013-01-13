package org.vento.crawler.twitter.processor;


import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.impl.DefaultMessage
import org.junit.Before
import org.junit.Test
import org.vento.model.Twit

public class TwitterPreprocessorTest {

    Exchange exchange;

    private TwitterPreprocessor target

    @Before
    public void setUp() throws Exception {
        target = new TwitterPreprocessor()

        CamelContext context = new DefaultCamelContext();
        exchange = new DefaultExchange(context);
    }

    @Test
    public void testProcessEnd2End() throws Exception {
        String bodyIn = getClass().getResourceAsStream('twitter-response-body.json').getText();
        Message messageIn = new DefaultMessage();
        messageIn.setBody(bodyIn)
        exchange.setIn(messageIn)

        target.process(exchange);

        def body = exchange.getOut().getBody()
        assert body != null

        assert body.twits.size() == 15
        assert body.twits.get(0).twitterId == '123505817893863425'
        assert body.twits.get(14).text == 'My better is better than your better Nike'
    }

    @Test
    public void textProcessing() {
        assert target.textProcessing("\"The Future of Google Maps: You'll Never Be Lost Again\": http://t.co/xXYRSrSi") ==
                "\"The Future of Google Maps: You'll Never Be Lost Again\":"
        assert target.textProcessing("@lddio c'è un certo #berlusconi che dopo la recente performance a #serviziopubblico vorrebbe confrontarsi con te...") ==
                "c'è un certo berlusconi che dopo la recente performance a serviziopubblico vorrebbe confrontarsi con te..."
    }

    @Test
    public void testAnalyzeAndCleanEmotionsPositive() {
        Twit t = new Twit(
                twitterId: '12345',
                text: 'bao :) :-)',
                referenceScore: null
        )

        Twit result = target.analyzeAndCleanEmotions(t)
        assert result.referenceScore == '3.0'
        assert result.text == 'bao'
    }

    @Test
    public void testAnalyzeAndCleanEmotionsNegative() {
        Twit t = new Twit(
                twitterId: '12345',
                text: ':( bao ',
                referenceScore: null
        )

        Twit result = target.analyzeAndCleanEmotions(t)
        assert result.referenceScore == '1.0'
        assert result.text == 'bao'
    }

    @Test
    public void testAnalyzeAndCleanEmotionsNegativeDouble() {
        Twit t = new Twit(
                twitterId: '12345',
                text: ':( bao :-(',
                referenceScore: null
        )

        Twit result = target.analyzeAndCleanEmotions(t)
        assert result.referenceScore == '1.0'
        assert result.text == 'bao'
    }

    @Test
    public void testAnalyzeAndCleanEmotionsRemoveSpaces() {
        Twit t = new Twit(
                twitterId: '12345',
                text: 'Ciao :) come :D va a tutti un :-F bao :) :-)',
                referenceScore: null
        )

        Twit result = target.analyzeAndCleanEmotions(t)
        assert result.text == 'Ciao  come  va a tutti un  bao'
        assert result.referenceScore == '3.0'
    }

    @Test
    public void testAnalyzeAndCleanEmotionsMixed() {
        Twit t = new Twit(
                twitterId: '12345',
                text: 'Ciao :( come :( va a tutti un :-F bao :) :-)',
                referenceScore: null
        )

        Twit result = target.analyzeAndCleanEmotions(t)
        assert result.text == 'Ciao  come  va a tutti un  bao'
        assert result.referenceScore == null
    }

    @Test
    public void testAnalyzeAndCleanEmotionsDirty() {
        Twit t = new Twit(
                twitterId: '12345',
                text: 'I just sliced 539 fruit in Classic Mode on Fruit Ninja for iPhone!looool:):D http://t.co/7m8i42hl http://t.co/AdIvW0CS',
                referenceScore: null
        )

        Twit result = target.analyzeAndCleanEmotions(t)
        assert result.text == 'I just sliced 539 fruit in Classic Mode on Fruit Ninja for iPhone!looool http://t.co/7m8i42hl http://t.co/AdIvW0CS'
        assert result.referenceScore == "3.0"
    }

    @Test
    public void testAnalyzeAndCleanEmotionsDirty2() {
        Twit t = new Twit(
                twitterId: '12345',
                text: 'Fantozzi :-}, fantozzi, =) dirty -) duneday =)Fancy :D Dancing :)',
                referenceScore: null
        )

        Twit result = target.analyzeAndCleanEmotions(t)
        assert result.text == 'Fantozzi , fantozzi,  dirty  duneday Fancy  Dancing'
        assert result.referenceScore == "3.0"
    }


}
