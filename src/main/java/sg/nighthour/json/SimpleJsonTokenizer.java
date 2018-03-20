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
 * Simple json tokenizer class.  To create a ArrayList of json tokens from an input string that
 * can be further processed by a SimpleJson Parser
 * 
 * Ng Chiang Lin
 * Apr 2016
 * 
 */
package sg.nighthour.json;

import java.util.ArrayList;

public class SimpleJsonTokenizer
{

    private int index;
    private ArrayList<String> tokens;
    private int tokens_index;

    public SimpleJsonTokenizer()
    {
        index = 0;
        tokens = new ArrayList<String>();
        tokens_index = 0;
    }

    /**
     * To peek at the next json token without consuming the token.
     * 
     * @return Json token or null if no more token is present in the arraylist
     */
    public String peekNext()
    {

        if (tokens_index < tokens.size() && tokens_index >= 0)
        {
            return tokens.get(tokens_index);
        }

        return null;
    }

    /**
     * Get the next json token
     * 
     * @return Json token or null if no more token is present in the arraylist
     */
    public String nextToken()
    {
        String ret = null;

        if (tokens_index < tokens.size() && tokens_index >= 0)
        {
            ret = tokens.get(tokens_index);
            tokens_index++;
        }

        return ret;
    }

    /**
     * To obtain the previous token
     * 
     * @return Previous json token or null if there is no previous token
     */
    public String prevToken()
    {
        String ret = null;
        if (tokens_index <= tokens.size() && tokens_index >= 1)
        {
            ret = tokens.get(tokens_index - 1);
        }
        return ret;
    }

    /**
     * To check if there is a next token in the array list.
     * 
     * @return true if there is available token, false otherwise
     */
    public boolean hasNext()
    {
        if (tokens_index < tokens.size() && tokens_index >= 0)
        {
            return true;
        }

        return false;
    }

    /**
     * Check if the token arraylist is empty
     * 
     * @return true if the token arraylist is empty, false otherwise
     */

    public boolean isEmpty()
    {
        return tokens.isEmpty();
    }

    /**
     * Skip the white spaces in the input String
     * 
     * @param input
     *            The json input to skip white spaces
     */
    private void skipSpace(String input)
    {
        if (input == null)
        {
            return;
        }

        if (index < input.length() && index >= 0)
        {
            char c = input.charAt(index);
            while (c == ' ' || c == '\t' || c == '\n' || c == '\r')
            {
                index++;
                if (index >= input.length() || index <= 0)
                { // handle the case where the last char of input is a space
                  // less than or equal zero check is for defensive coding , to
                  // prevent wrap around if large no. of spaces.
                    break;
                }
                c = input.charAt(index);
            }
        }

    }

    /**
     * Tokenizes the input string and place all the legal json tokens into an
     * tokens array list
     * 
     * @param input
     *            The json input to tokenize
     * @throws SimpleTokenException
     */
    public void tokenize(String input) throws SimpleTokenException
    {
        if (input == null)
        {// nothing to tokenize
            return;
        }

        index = 0;
        skipSpace(input); // skip any leading spaces
        tokens = new ArrayList<String>(); // reinitialize tokens
        tokens_index = 0; // reinitialize the tokens_index for the token
                          // arrayList as well

        while (index < input.length() && index >= 0)
        {

            char c = input.charAt(index);

            switch (c)
            {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    skipSpace(input);
                    break;
                case '{':
                case '}':
                case ':':
                case ',':
                case '[':
                case ']':
                    tokens.add(Character.toString(c));
                    index++;
                    break;
                case '"':
                    tokens.add(getJsonString(input));
                    break;
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
                    tokens.add(getJsonNumber(input, false));
                    break;
                case '-':
                    tokens.add(getJsonNumber(input, true));
                    break;
                case 'n':
                case 't':
                case 'f':
                    tokens.add(getNullTrueFalse(input));
                    break;
                default:
                    throw new SimpleTokenException("Invalid token");

            }

        }

    }

    /**
     * Retrieve the json tokens for true, false or null
     * 
     * @param input
     *            The json input
     * @return json token for true, false or null
     * @throws SimpleTokenException
     */
    private String getNullTrueFalse(String input) throws SimpleTokenException
    {

        if (input == null || index >= input.length() || index < 0)
        {
            throw new SimpleTokenException("Invalid null/true/false tokenization error");
        }

        StringBuffer sb = new StringBuffer();
        char c = input.charAt(index);
        switch (c)
        {
            case 'n':
                // Check that input can contain the full 4 char word "null"
                if ((index + 4) > input.length())
                {
                    throw new SimpleTokenException("Invalid null/true/false tokenization error");
                }
                String str_null = input.substring(index, index + 4);
                if ("null".equals(str_null))
                {
                    sb.append(str_null);
                }
                index = index + 4;
                break;
            case 't':
                // Check that input can contain the full 4 char word "true"
                if ((index + 4) > input.length())
                {
                    throw new SimpleTokenException("Invalid null/true/false tokenization error");
                }
                String str_true = input.substring(index, index + 4);
                if ("true".equals(str_true))
                {
                    sb.append(str_true);
                }
                index = index + 4;
                break;
            case 'f':
                // Check that input can contain the full 5 char word "false"
                if ((index + 5) > input.length())
                {
                    throw new SimpleTokenException("Invalid null/true/false tokenization error");
                }
                String str_false = input.substring(index, index + 5);
                if ("false".equals(str_false))
                {
                    sb.append(str_false);
                }
                index = index + 5;
                break;

            default:
                throw new SimpleTokenException("Invalid null/true/false tokenization error");
        }

        return sb.toString();
    }

    /*
     * Long method that can be complex to read and understand. Can be simplified
     * in future.
     */

    /**
     * Retrieve json token for number
     * 
     * @param input
     * @param neg,
     *            set to true if negative sign is present
     * @return json token for number
     * @throws SimpleTokenException
     */
    private String getJsonNumber(String input, boolean neg) throws SimpleTokenException
    {
        if (input == null || index >= input.length() || index < 0)
        {
            throw new SimpleTokenException("Invalid Number tokenization error");
        }

        StringBuffer sb = new StringBuffer();
        char c = input.charAt(index);
        sb.append(c);
        index++;

        boolean done = false;
        boolean decimal = false;
        boolean exponential = false;
        boolean exponential_sign = false;

        if (c == '0')
        {// number cannot have leading zero apart from decimal or zero itself
         // eg. 0001234
            if (index >= input.length() || index < 0)
            {
                throw new SimpleTokenException("Invalid Number tokenization error");
            }

            char nc = input.charAt(index);
            switch (nc)
            {
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
                    throw new SimpleTokenException("Invalid Number tokenization error leading zero");
                default:
                    break;
            }
        }

        while (!done)
        {
            if (index >= input.length() || index < 0)
            {
                throw new SimpleTokenException("Invalid Number tokenization error");
            }

            c = input.charAt(index);

            if (Character.isDigit(c))
            {
                sb.append(c);
                index++;

                if (neg)
                {// clear the negative flag, since now a digit has added
                 // directly after the minus sign
                    neg = false;
                    // check for leading zero after negative sign -00001
                    if (c == '0')
                    {
                        if (index >= input.length() || index < 0)
                        {
                            throw new SimpleTokenException("Invalid Number tokenization error");
                        }

                        char nc = input.charAt(index);
                        switch (nc)
                        {
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
                                throw new SimpleTokenException("Invalid Number tokenization error leading zero");
                            default:
                                break;
                        }
                    }

                }
            }
            else if (c == '.' && !neg)
            {// decimal case and not a minus sign in previous char, if minus
             // sign needs to follow by a digit and not a dot.

                if (!decimal)
                {// first time dot occur
                    sb.append(c);
                    decimal = true;

                    // Get the first char after the dot, should be another digit
                    // for valid decimal
                    index++;
                    if (index >= input.length() || index < 0)
                    {
                        throw new SimpleTokenException("Invalid Number tokenization error");
                    }

                    c = input.charAt(index);

                    if (Character.isDigit(c))
                    {
                        sb.append(c);
                        index++;
                    }
                    else
                    {
                        throw new SimpleTokenException("Invalid Number (decimal) tokenization error");
                    }
                }
                else
                {// dot already occur previously
                    throw new SimpleTokenException("Invalid Number (decimal) tokenization error");
                }

            }
            else if ((c == 'e' || c == 'E') && !neg && !exponential)
            {// handle exponential

                sb.append(c);
                index++;
                exponential = true;
                boolean innerdone = false;
                boolean added_digit_after_e = false;
                boolean added_digit_after_e_sign = false;
                while (!innerdone)
                {
                    if (index >= input.length() || index < 0)
                    {
                        throw new SimpleTokenException("Invalid Number (exponential) tokenization error");
                    }

                    char ch = input.charAt(index);

                    if (Character.isDigit(ch))
                    {
                        sb.append(ch);
                        index++;
                        added_digit_after_e = true;
                        if (exponential_sign)
                        {
                            added_digit_after_e_sign = true;
                        }
                    }
                    else if ((ch == '-' || ch == '+') && !exponential_sign)
                    { // negative or positive exponential
                        sb.append(ch);
                        index++;
                        exponential_sign = true;

                    }
                    else
                    {
                        innerdone = true;
                    }

                }

                if (!added_digit_after_e && !added_digit_after_e_sign)
                {// nothing added after e or E , format is wrong
                    throw new SimpleTokenException("Invalid Number (exponential) tokenization error");
                }

            }
            else if ((c == ',' || c == '}' || c == ']' || Character.isWhitespace(c)) && !neg)
            { // non number , don't increment index, these 3 chars can be
              // terminator for number, whitespace as well can terminate a
              // number
                done = true;
            }
            else
            {// non number, illegal char here, number shouldn't be followed by
             // these.
                throw new SimpleTokenException("Invalid Number(termination) tokenization error");
            }

        }

        return sb.toString();

    }

    /**
     * Get the escape unicode characters /uHHHH that can be present in a json
     * string token
     * 
     * @param input
     * @return json string unicode escape sequence
     * @throws SimpleTokenException
     */
    private String getJsonStringEscapeSequenceHex(String input) throws SimpleTokenException
    {
        index++;
        if (input == null || index >= input.length() || index < 0)
        {
            throw new SimpleTokenException("Invalid String tokenization (escape sequence \\u hexidecimal) error");
        }

        StringBuffer sb = new StringBuffer();
        sb.append('u');

        for (int i = 0; i < 4; i++)
        {
            if (index >= input.length() || index < 0)
            {
                throw new SimpleTokenException("Invalid String tokenization (escape sequence \\u hexidecimal) error");
            }
            char c = input.charAt(index);

            switch (c)
            {
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
                case 'A':
                case 'a':
                case 'B':
                case 'b':
                case 'C':
                case 'c':
                case 'D':
                case 'd':
                case 'E':
                case 'e':
                case 'F':
                case 'f':
                    sb.append(c);
                    index++;
                    break;
                default:
                    throw new SimpleTokenException(
                            "Invalid String tokenization (escape sequence \\u hexidecimal) error");

            }

        }

        return sb.toString();

    }

    /**
     * Get the json escape sequences \n, \\, \" etc.. in the json string
     * 
     * @param input
     * @return json escape sequence
     * @throws SimpleTokenException
     */
    private String getJsonStringEscapeSequence(String input) throws SimpleTokenException
    {
        index++;
        if (input == null || index >= input.length() || index < 0)
        {
            throw new SimpleTokenException("Invalid String tokenization (escape sequence) error");
        }
        StringBuffer sb = new StringBuffer();
        sb.append('\\');
        char c = input.charAt(index);

        switch (c)
        {
            case '"':
            case '\\':
            case '/':
            case 'b':
            case 'f':
            case 'n':
            case 'r':
            case 't':
                sb.append(c);
                index++;
                break;
            case 'u':
                sb.append(getJsonStringEscapeSequenceHex(input));
                break;

            default:
                throw new SimpleTokenException("Invalid String tokenization (escape sequence) error");
        }

        return sb.toString();

    }

    /**
     * Retrieve the json string token
     * 
     * @param input
     * @return json string token
     * @throws SimpleTokenException
     */
    private String getJsonString(String input) throws SimpleTokenException
    {

        if (input == null || index >= input.length() || index < 0)
        {
            throw new SimpleTokenException("Invalid String tokenization error");
        }

        StringBuffer sb = new StringBuffer();
        char c = input.charAt(index);
        sb.append(c); // add the quotation, since string starts with quotation
                      // and ends with quotation
        index++;
        boolean done = false;
        while (!done)
        {
            if (index >= input.length() || index < 0)
            {
                throw new SimpleTokenException("Invalid String tokenization error");
            }

            c = input.charAt(index);

            if (c == '\\')
            { // check for escape sequence
                sb.append(getJsonStringEscapeSequence(input));
                continue;
            }
            else if (Character.isISOControl(c))
            {// Control characters throw exception
                throw new SimpleTokenException("Invalid String tokenization error, control chars");
            }
            else if (c == '"')
            {
                done = true;
            }

            sb.append(c);
            index++;
        }

        return sb.toString();
    }

    /**
     * Print out all the json tokens in the array list
     */
    public void printTokens()
    {

        for (String t : tokens)
        {
            System.out.println(t);
        }
    }

}
