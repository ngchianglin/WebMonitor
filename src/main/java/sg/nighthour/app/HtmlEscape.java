/*
* MIT License
*
*Copyright (c) 2018 Ng Chiang Lin
*
*Permission is hereby granted, free of charge, to any person obtaining a copy
*of this software and associated documentation files (the "Software"), to deal
*in the Software without restriction, including without limitation the rights
*to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
*copies of the Software, and to permit persons to whom the Software is
*furnished to do so, subject to the following conditions:
*
*The above copyright notice and this permission notice shall be included in all
*copies or substantial portions of the Software.
*
*THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
*IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
*FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
*AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
*LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
*OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
*SOFTWARE.
*
*/

package sg.nighthour.app;

public class HtmlEscape
{

    private static final int BUFOFFSET = 32;

    /**
     * For escaping UTF-8 encoded html content. ", ', &, < , > Should not be used
     * for html attributes, inside script tags etc...
     * 
     * @param str
     * @return escaped str
     */
    public static String escapeHTML(String str)
    {

        StringBuilder buf = new StringBuilder(str.length() + BUFOFFSET);

        for (int i = 0; i < str.length(); i++)
        {
            char c = str.charAt(i);

            switch (c)
            {
                case '"':
                    buf.append("&quot;");
                    break;
                case '\'':
                    buf.append("&#39;");
                    break;
                case '&':
                    buf.append("&amp;");
                    break;
                case '<':
                    buf.append("&lt;");
                    break;
                case '>':
                    buf.append("&gt;");
                    break;
                default:
                    buf.append(c);
                    break;

            }

        }

        return buf.toString();

    }

    /**
     * For utf-8 content only, unescaping html entities &quot; &#39; &amp; &lt; &gt;
     * back to " ' & < > It is the reverse of escapeHTML() method. Note it does not
     * escape all forms of html entities, only the entities encoded by escapeHTML()
     * method
     * 
     */
    public static String unescapeHTML(String str)
    {

        StringBuilder buf = new StringBuilder(str.length() + BUFOFFSET);

        int strlen = str.length();
        int index = 0;
        while (index < strlen)
        {
            char c = str.charAt(index);

            if (c == '&')
            {// potential entity
             // peek ahead next char to determine the length
             // if it is an entity that we are interested in
                char nextc = str.charAt(index + 1);

                int sublen = 0;
                String tmp = "";

                switch (nextc)
                {
                    case 'q':
                        sublen = 6;
                        tmp = str.substring(index, index + sublen);
                        if (tmp.equalsIgnoreCase("&quot;"))
                        {
                            buf.append("\"");
                            index = index + sublen;
                        }
                        break;
                    case '#':
                        sublen = 5;
                        tmp = str.substring(index, index + sublen);
                        if (tmp.equalsIgnoreCase("&#39;"))
                        {
                            buf.append("\'");
                            index = index + sublen;
                        }
                        break;
                    case 'a':
                        sublen = 5;
                        tmp = str.substring(index, index + sublen);
                        if (tmp.equalsIgnoreCase("&amp;"))
                        {
                            buf.append("&");
                            index = index + sublen;
                        }
                        break;
                    case 'l':
                        sublen = 4;
                        tmp = str.substring(index, index + sublen);
                        if (tmp.equalsIgnoreCase("&lt;"))
                        {
                            buf.append("<");
                            index = index + sublen;
                        }
                        break;

                    case 'g':
                        sublen = 4;
                        tmp = str.substring(index, index + sublen);
                        if (tmp.equalsIgnoreCase("&gt;"))
                        {
                            buf.append(">");
                            index = index + sublen;
                        }
                        break;
                    default:
                        // not our entity
                        buf.append(c);
                        index++;
                        break;
                }

            }
            else
            {// not an entity
                buf.append(c);
                index++;
            }

        }

        return buf.toString();

    }

}
