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
 * Simple class to represent a Json Object
 * 
 * Ng Chianglin
 * Apr 2016
 * 
 */

package sg.nighthour.json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class SimpleJsonObject
{

    private HashMap<String, SimpleJsonValue> namevaluepairs;

    public SimpleJsonObject()
    {
        namevaluepairs = new HashMap<String, SimpleJsonValue>();
    }

    /**
     * Retrieves the value stored in the name value pairs of the json object
     * based on the name parameter
     * 
     * @param name
     *            The name of the corresponding json name value pair
     * @return json value or null if not present
     */
    public SimpleJsonValue getValue(String name)
    {
        return namevaluepairs.get(name);
    }

    /**
     * Add a name value pair in the json object.
     * 
     * @param name
     *            The name of the name value pair
     * @param value
     *            The json value of the name value pair
     */
    public void setNameValue(String name, SimpleJsonValue value)
    {
        namevaluepairs.put(name, value);
    }

    /**
     * Check if the name value pairs are empty
     * 
     * @return true if empty, false otherwise
     */
    public boolean isEmpty()
    {
        return namevaluepairs.isEmpty();
    }

    /**
     * Returns the string format of the json object
     * 
     * @return json object
     */
    @Override
    public String toString()
    {
        Set<String> keys = namevaluepairs.keySet();
        Iterator<String> iter = keys.iterator();
        StringBuffer sb = new StringBuffer();
        String name = null;
        sb.append("{");
        while (iter.hasNext())
        {
            name = iter.next();
            if (name != null)
            {
                sb.append("\"" + name + "\":" + namevaluepairs.get(name));
            }

            if (iter.hasNext())
            {
                sb.append(",");
            }

        }

        sb.append("}");

        return sb.toString();
    }

}
