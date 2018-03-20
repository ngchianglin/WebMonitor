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
 * Simple Json Parser. 
 * To parse json input into objects that can be used much more easily in an application. 
 * The Parser uses mutual recursion technique. 
 * 
 * This simple parser is written as a programming exercise to learn more about parsers. 
 * The json format offers a simple enough grammar for writing a tokenizer and parser using simple mutual recursion technique.
 * By writing my own simple parser, it not only provides a learning exercise, the parser can be used 
 * in my own java applications, servlet/jsp web applications without relying on 
 * external third party libraries for cases where it is suitable and makes sense. 
 * 
 * SimpleJsonParser has been tested using the test json files that are available from 
 * http://www.json.org/JSON_checker/ 
 * 
 * Take note that fail18.json which tests depth of nesting [[[]]] will parse successfully
 * using SimpleJsonParser. SimpleJsonParser currently doesn't have a default depth setting. 
 *  
 * A maximum input String size setting can be specified for SimpleJsonParser to reject input that exceed certain size. 
 * 
 * A Findbugs check has also been ran against SimpleJsonParser and so far no serious bugs detected for the parser package. 
 * Reference: http://findbugs.sourceforge.net/
 * 
 * For more information about json format and grammar
 * References: http://www.json.org
 * 
 * Do note that Java EE provides API for json parsing and processing. 
 * https://jcp.org/en/jsr/detail?id=353
 * Other popular java third party libraries are also available for Json processing. 
 * 
 * Ng Chiang Lin
 * Apr 2016
 * 
 */

package sg.nighthour.json;

public class SimpleJsonParser
{

    private SimpleJsonTokenizer tokenizer;
    private String input;
    private static final int MAXINPUTSIZE = sg.nighthour.app.AppConstants.MAX_JSON_PARSER_INPUT_SIZE;
    // private int parseArrayCnt=0;
    // private int parseArrayLeftBracket = 0;

    /**
     * Initialize a SimpleJsonParser object
     * 
     * @param input
     *            The json input to be parsed
     */
    public SimpleJsonParser(String input)
    {
        this.input = input;
        tokenizer = new SimpleJsonTokenizer();
    }

    /**
     * Parses the json tokens input into a json object or array
     * 
     * @return Json value which can contain a json object or array
     * @throws SimpleParseException
     * @throws SimpleTokenException
     */
    public SimpleJsonValue parse() throws SimpleParseException, SimpleTokenException
    {
        if (input == null)
        {
            throw new SimpleParseException("Null input");
        }

        if (input.length() > MAXINPUTSIZE)
        {
            throw new SimpleParseException("Exceeded Maximum Input Size " + MAXINPUTSIZE);
        }

        SimpleJsonValue val = null;
        tokenizer.tokenize(input);

        String str = tokenizer.peekNext();
        // tokenizer.printTokens();

        if (str.equals("{"))
        { // Json Object
            val = new SimpleJsonValue(parseObject());
            if (tokenizer.hasNext())
            {// Trailing data is present eg {"name1":"value1"}]:}]]]]
                throw new SimpleParseException("Parse error invalid json object trailing data");
            }
        }
        else if (str.equals("["))
        { // Json Array
            val = new SimpleJsonValue(parseArray());
            if (tokenizer.hasNext())
            {// Trailing data is present eg [123,abc]]]]]
                throw new SimpleParseException("Parse error invalid json array trailing data");
            }
        }
        else
        {
            throw new SimpleParseException("Parse error invalid json format");
        }

        return val;
    }

    /**
     * Parses json tokens input into a json array
     * 
     * @return Json value containing a json array
     * @throws SimpleParseException
     */
    private SimpleJsonArray parseArray() throws SimpleParseException
    {
        SimpleJsonArray arr = new SimpleJsonArray();

        boolean done = false;

        while (!done)
        {

            String token = tokenizer.peekNext();

            if (token == null)
            {
                throw new SimpleParseException("Parse json array exception");
            }

            if (token.equals("["))
            {
                tokenizer.nextToken(); // consume [
                SimpleJsonValue val = getValue();
                if (val != null)
                {// cater for the case of empty array []
                    arr.addValue(val);
                }

            }
            else if (token.equals(","))
            {
                tokenizer.nextToken(); // consume ,
                arr.addValue(getValue());
            }
            else if ("]".equals(token))
            {
                tokenizer.nextToken(); // consume ]
                done = true;

            }
            else
            {
                throw new SimpleParseException("Parse json array exception");
            }
        }

        return arr;

    }

    /**
     * Retrieves a json value from the json tokens input
     * 
     * @return a json value that can be a json string, number, object, array,
     *         true, false or json null value. Method will return a java null
     *         object if there is no json value, eg. []
     * @throws SimpleParseException
     */
    private SimpleJsonValue getValue() throws SimpleParseException
    {
        SimpleJsonValue ret = new SimpleJsonValue();

        String token = tokenizer.peekNext();
        if (token == null)
        {
            throw new SimpleParseException("Parse json getValue exception");
        }

        if (token.startsWith("\""))
        {// String type
            ret.setType(JsonType.STRING);
            ret.setJsonString(getJsonString());
        }
        else if (isNumber(token))
        {// Number type
            ret.setType(JsonType.NUMBER);
            ret.setJsonNumber(token);
            tokenizer.nextToken(); // consume the number
        }
        else if (token.startsWith("{"))
        {// Json Object
            ret.setType(JsonType.OBJECT);
            ret.setJsonObject(parseObject()); // parseObject will consume the {
        }
        else if (token.startsWith("["))
        {// Json Array
            ret.setType(JsonType.ARRAY);
            ret.setJsonArray(parseArray());// parseArray will consume the [
        }
        else if ("true".equals(token) || "false".equals(token))
        {// Json boolean
            ret.setType(JsonType.BOOLEAN);
            ret.setJsonBoolean(token);
            tokenizer.nextToken(); // consume the boolean
        }
        else if ("null".equals(token))
        {// Json null
            ret.setType(JsonType.NULL);
            ret.setJsonNull(token);
            tokenizer.nextToken(); // consume the null
        }
        else if ("]".equals(token) && "[".equals(tokenizer.prevToken()))
        {
            ret = null;
        }
        else
        {
            throw new SimpleParseException("Parse json getValue exception");
        }

        return ret;
    }

    /**
     * Parses the json tokens input into a json object
     * 
     * @return json object
     * @throws SimpleParseException
     */
    private SimpleJsonObject parseObject() throws SimpleParseException
    {
        SimpleJsonObject jsObj = new SimpleJsonObject();

        boolean done = false;

        while (!done)
        {
            String token = tokenizer.peekNext();

            if (token == null)
            {
                throw new SimpleParseException("Parse json object exception");
            }

            if ("{".equals(token))
            {
                tokenizer.nextToken(); // consume the {
                SimpleJsonNamePair np = parseNamePair();
                if (np != null)
                {// cater for the case of empty json object {}
                    jsObj.setNameValue(np.getName(), np.getValue());
                }
            }
            else if ("}".equals(token))
            {
                done = true;
                tokenizer.nextToken(); // consume the }
            }
            else if (",".equals(token))
            {
                tokenizer.nextToken(); // consume the ,
                SimpleJsonNamePair np = parseNamePair();
                if (np == null)
                { // To ensure that the namepair is null
                    throw new SimpleParseException("Parse json object exception");
                }

                jsObj.setNameValue(np.getName(), np.getValue());
            }
            else
            {
                throw new SimpleParseException("Parse json object exception");
            }
        }

        return jsObj;

    }

    /**
     * Parses the json tokens input into a json name value pair
     * 
     * @return Json name value pair or null if empty object with no namepair {}
     * @throws SimpleParseException
     */
    private SimpleJsonNamePair parseNamePair() throws SimpleParseException
    {
        SimpleJsonNamePair namepair = new SimpleJsonNamePair();

        boolean done = false;
        boolean emptystring = true;

        while (!done)
        {
            String token = tokenizer.peekNext();
            if (token == null)
            {
                throw new SimpleParseException("Parse json name pair exception");
            }

            if (token.startsWith("\""))
            {// Json string
                namepair.setName(getJsonString());
                emptystring = false;
            }
            else if (":".equals(token))
            {
                if (emptystring)
                {// no preceding json string
                    throw new SimpleParseException("Parse json name pair exception");
                }
                tokenizer.nextToken(); // consume the :
                namepair.setValue(getValue());
                done = true;
            }
            else if ("}".equals(token) && "{".equals(tokenizer.prevToken()))
            {// empty object
                done = true;
                namepair = null;
            }
            else
            {
                throw new SimpleParseException("Parse json name pair exception");
            }

        }

        return namepair;

    }

    /**
     * Determines if a Json token is a valid json number
     * 
     * @param token
     *            The token to be checked
     * @return true if it is json number, false otherwise
     * @throws SimpleParseException
     */
    private boolean isNumber(String token) throws SimpleParseException
    {
        boolean ret = false;

        if (token == null || token.length() == 0)
        {
            throw new SimpleParseException("Parse json getValue Number exception");
        }

        char c = token.charAt(0);

        switch (c)
        {
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                ret = true;
                break;
            default:
                ret = false;
        }

        return ret;

    }

    /**
     * Retrieves a json string from the json tokens input
     * 
     * @return json string
     * @throws SimpleParseException
     */
    private String getJsonString() throws SimpleParseException
    {
        String ret = tokenizer.nextToken();

        if (ret == null)
        {
            throw new SimpleParseException("Parse json name pair getString exception");
        }

        if (ret.length() > 1)// Check that shortest string token is ""
        {
            ret = ret.substring(1, ret.length() - 1); // remove the leading and
                                                      // trailing quotation
        }
        else
        {
            throw new SimpleParseException("Parse json name pair getString exception");
        }

        return ret;
    }

}
