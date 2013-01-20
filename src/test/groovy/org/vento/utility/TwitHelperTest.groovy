package org.vento.utility;

import org.junit.Test
import org.vento.model.Twit;

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 20/01/13
 * Time: 08:56
 * To change this template use File | Settings | File Templates.
 */
public class TwitHelperTest {

    @Test
    public void testAnalyzeAndCleanEmotionsPositive() {
        Twit t = new Twit(
                twitterId: '12345',
                text: 'bao :) :-)',
                referenceScore: null
        )

        Twit result = TwitHelper.analyzeAndCleanEmotions(t)
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

        Twit result = TwitHelper.analyzeAndCleanEmotions(t)
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

        Twit result = TwitHelper.analyzeAndCleanEmotions(t)
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

        Twit result = TwitHelper.analyzeAndCleanEmotions(t)
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

        Twit result = TwitHelper.analyzeAndCleanEmotions(t)
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

        Twit result = TwitHelper.analyzeAndCleanEmotions(t)
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

        Twit result = TwitHelper.analyzeAndCleanEmotions(t)
        assert result.text == 'Fantozzi , fantozzi,  dirty  duneday Fancy  Dancing'
        assert result.referenceScore == "3.0"
    }

}
