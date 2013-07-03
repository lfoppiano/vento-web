package org.vento.utility;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 20/01/13
 * Time: 08:55
 * To change this template use File | Settings | File Templates.
 */
public class StringProcessorTest {

    @Test
    public void testTextProcessing() throws Exception {
        assert StringProcessor.textProcessing("\"The Future of Google Maps: You'll Never Be Lost Again\": http://t.co/xXYRSrSi") ==
                "\"The Future of Google Maps: You'll Never Be Lost Again\": _LINK_"
    }

    @Test
    public void testTextProcessing2() throws Exception {
        assert StringProcessor.textProcessing("@lddio c'è un certo #berlusconi che dopo la recente performance a #serviziopubblico vorrebbe confrontarsi con te...") ==
                "_USER_ c'è un certo _TAG_ che dopo la recente performance a _TAG_ vorrebbe confrontarsi con te..."
    }

    @Test
    public void testTextProcessing3() throws Exception {
        assert StringProcessor.textProcessing("RT: IL FATTO: Nadia Macrì: Berlusconi a letto ha grandi capacità, mi manca tantissimo. NO COMMENT!! http://t.co/H1nHraC8") ==
                "_RETWEET_ IL FATTO: Nadia Macrì: Berlusconi a letto ha grandi capacità, mi manca tantissimo. NO COMMENT!! _LINK_"
    }
}
