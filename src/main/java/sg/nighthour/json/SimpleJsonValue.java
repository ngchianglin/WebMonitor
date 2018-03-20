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
 * Simple class to represent a Json value,, which can be a string, number, object, array , true, false or null
 * JsonType enum represents the various types. 
 * true and false are represented by JsonType.BOOLEAN. 
 * 
 * Ng Chianglin
 * Apr 2016
 * 
 */

package sg.nighthour.json;

public class SimpleJsonValue
{

    JsonType type;
    private String jsonstring;
    private String jsonnumber;
    private SimpleJsonObject jsonobject;
    private SimpleJsonArray jsonarray;
    private String jsonboolean;
    private String jsonnull;

    public SimpleJsonValue()
    {
        jsonstring = null;
        jsonnumber = null;
        jsonobject = null;
        jsonarray = null;
        jsonboolean = null;
        jsonnull = null;
    }

    /**
     * Constructor to create a SimpleJsonValue that holds a SimpleJsonObject
     * 
     * @param obj
     *            json object
     */
    public SimpleJsonValue(SimpleJsonObject obj)
    {
        type = JsonType.OBJECT;
        jsonobject = obj;
    }

    /**
     * Constructor to create a SimpleJsonValue that holds a SimpleJsonArray
     * 
     * @param arr
     *            json array
     */
    public SimpleJsonValue(SimpleJsonArray arr)
    {
        type = JsonType.ARRAY;
        jsonarray = arr;
    }

    /**
     * Constructor to a SimpleJsonValue according to the pass in type STRING,
     * NUMBER, BOOLEAN, NULL
     * 
     * @param input
     *            Input containing the json value
     * @param type
     *            JsonType can be STRING, NUMBER, BOOLEAN, NULL
     */
    public SimpleJsonValue(String input, JsonType type)
    {

        switch (type)
        {
            case STRING:
                this.type = type;
                this.jsonstring = input;
                break;
            case NUMBER:
                this.type = type;
                this.jsonnumber = input;
                break;
            case BOOLEAN:
                this.type = type;
                setJsonBoolean(input);
                break;
            case NULL:
                this.type = type;
                setJsonNull(input);
                break;
            default:
                break;
        }

    }

    /**
     * Set the type of the json value, which can be a string, number, object,
     * array , true, false or null true and false are represented by
     * JsonType.BOOLEAN.
     * 
     * @param type
     *            The type of the json value.
     */
    public void setType(JsonType type)
    {
        this.type = type;
    }

    /**
     * Retrieves the type of the json value
     * 
     * @return json type
     */
    public JsonType getType()
    {
        return type;
    }

    /**
     * Set the json string for a string type value
     * 
     * @param s
     *            json string
     */
    public void setJsonString(String s)
    {
        jsonstring = s;
    }

    /**
     * Retrieve the json string
     * 
     * @return json string
     */
    public String getJsonString()
    {
        return jsonstring;
    }

    /**
     * Set the json number for a number type value
     * 
     * @param num
     *            json number
     */
    public void setJsonNumber(String num)
    {
        jsonnumber = num;
    }

    /**
     * Retrieves the json number for a number type value
     * 
     * @return json number
     */
    public String getJsonNumber()
    {
        return jsonnumber;

    }

    /**
     * Set the json object for a json object type value
     * 
     * @param jsonObj
     *            json object
     */
    public void setJsonObject(SimpleJsonObject jsonObj)
    {
        jsonobject = jsonObj;
    }

    /**
     * Retrieves the json object for a json object type value
     * 
     * @return json object
     */
    public SimpleJsonObject getJsonObject()
    {
        return jsonobject;
    }

    /**
     * Set the json array for a json array type value
     * 
     * @param arr
     *            json array
     */
    public void setJsonArray(SimpleJsonArray arr)
    {
        jsonarray = arr;
    }

    /**
     * Retrieves the json array from json array type value
     * 
     * @return json array
     */
    public SimpleJsonArray getJsonArray()
    {
        return jsonarray;
    }

    /**
     * Set a json boolean (true or false) for a json boolean (true or false)
     * type value
     * 
     * @param b
     *            json true or false
     */
    public void setJsonBoolean(String b)
    {
        if (b == null)
        {
            return;
        }

        if ("false".equals(b) || "true".equals(b))
        {
            jsonboolean = b;
        }
    }

    /**
     * Retrieves a json boolean (true or false) for a json boolean type value
     * 
     * @return json true or false
     */
    public String getJsonBoolean()
    {
        return jsonboolean;
    }

    /**
     * Set a json null for a json null type value
     * 
     * @param n
     *            json null
     */
    public void setJsonNull(String n)
    {
        if (n == null)
        {
            return;
        }

        if ("null".equals(n))
        {
            jsonnull = n;
        }
    }

    /**
     * Retrieves a json null
     * 
     * @return json null
     */
    public String getJsonNull()
    {
        return jsonnull;
    }

    /**
     * Returns the string format of the json value which be a json string,
     * number, object, array, true, false or null
     * 
     * @return String of the json value
     */
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        switch (type)
        {
            case STRING:
                if (jsonstring != null)
                {
                    sb.append("\"" + jsonstring + "\"");
                }
                break;
            case NUMBER:
                if (jsonnumber != null)
                {
                    sb.append(jsonnumber);
                }
                break;
            case OBJECT:
                if (jsonobject != null)
                {
                    sb.append(jsonobject.toString());
                }
                else
                {
                    sb.append("{}");
                }
                break;
            case ARRAY:
                if (jsonarray != null)
                {
                    sb.append(jsonarray.toString());
                }
                else
                {
                    sb.append("[]");
                }
                break;
            case BOOLEAN:
                if (jsonboolean != null)
                {
                    sb.append(jsonboolean);
                }
                break;
            case NULL:
                if (jsonnull != null)
                {
                    sb.append(jsonnull);
                }
                break;
            default:
                break;
        }

        return sb.toString();

    }

}
