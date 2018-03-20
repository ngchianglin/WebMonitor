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
 * Simple class to represent a Json array. 
 * 
 * Ng Chianglin
 * Apr 2016
 * 
 */
package sg.nighthour.json;

import java.util.ArrayList;

public class SimpleJsonArray
{

    private ArrayList<SimpleJsonValue> array;

    public SimpleJsonArray()
    {
        array = new ArrayList<SimpleJsonValue>();
    }

    /**
     * Add a Json value to the Json array
     * 
     * @param json
     *            value to be added to array
     */
    public void addValue(SimpleJsonValue val)
    {
        array.add(val);
    }

    /**
     * Retrieve a Json value from the Json array
     * 
     * @param index
     *            an integer index to the array
     * @return Json value or null if it doesn't exists at index
     */
    public SimpleJsonValue getValue(int index)
    {
        if (index < array.size() && index >= 0)
        {
            return array.get(index);
        }

        return null;
    }

    /**
     * Retrieve the size of the Json array
     * 
     * @return size of json array
     */
    public int getSize()
    {
        return array.size();
    }

    /**
     * Returns the string format of a json array
     * 
     * @return String format of json array
     */

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < array.size(); i++)
        {
            sb.append(array.get(i).toString());

            if (i != (array.size() - 1))
            {
                sb.append(",");
            }

        }
        sb.append("]");

        return sb.toString();

    }
}
