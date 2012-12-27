package org.vento.crawler.amazon.utility

/**
 * Created with IntelliJ IDEA.
 * User: lfoppiano
 * Date: 29/06/12
 * Time: 23.54
 * To change this template use File | Settings | File Templates.
 */
public class StringProcessor {
    public static String removeInvalidUtf8Chars(String inString) {
        if (inString == null) return null;

        StringBuilder newString = new StringBuilder();
        char ch;

        for (int i = 0; i < inString.size(); i++){
            ch = inString[i];
            if ((ch < 0x00FD && ch > 0x001F) || ch == '\t' || ch == '\n' || ch == '\r') {
                newString.append(ch);
            }
        }
        return newString.toString();

    }
}
