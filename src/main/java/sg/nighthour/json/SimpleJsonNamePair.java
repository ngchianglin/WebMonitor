/*
* MIT License
*
*Copyright (c) 2016 Ng Chiang Lin
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

/**
 * Simple class to represent a Json name value pair
 * 
 * Ng Chianglin
 * Apr 2016
 * 
 */

package sg.nighthour.json;

public class SimpleJsonNamePair
{
    private String name;
    private SimpleJsonValue value;

    /**
     * Retrives the name from the name value pair
     * 
     * @return name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of the name value pair
     * 
     * @param name
     *            The name of the name value pair
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Retrieves the json value of the name value pair
     * 
     * @return json value
     */
    public SimpleJsonValue getValue()
    {
        return value;
    }

    /**
     * Set the value of the name value pair
     * 
     * @param value
     *            Json value of the name value pair
     */
    public void setValue(SimpleJsonValue value)
    {
        this.value = value;
    }

}
