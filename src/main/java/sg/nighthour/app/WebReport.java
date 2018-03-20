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

public class WebReport
{

    private String url;
    private String domain;
    private String checksum;
    private String content;
    private String raw;

    public WebReport(String url, String checksum, String content, String raw)
    {
        this.url = url;
        this.checksum = checksum;
        this.content = content;
        this.raw = raw;
    }

    public WebReport(String url, String checksum, String content, String domain, String raw)
    {
        this.url = url;
        this.checksum = checksum;
        this.content = content;
        this.domain = domain;
        this.raw = raw;

    }

    public String getRaw()
    {
        return raw;
    }

    public String getURL()
    {
        return url;
    }

    public String getDomain()
    {
        return domain;
    }

    public String getCheckSum()
    {
        return checksum;
    }

    public String getContent()
    {
        return content;
    }

    public void setURL(String url)
    {
        this.url = url;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    public void setCheckSum(String checksum)
    {
        this.checksum = checksum;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public void setRaw(String raw)
    {
        this.raw = raw;
    }

}
