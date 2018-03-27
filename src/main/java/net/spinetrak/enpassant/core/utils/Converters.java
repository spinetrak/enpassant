/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2018 spinetrak
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.spinetrak.enpassant.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Converters
{
  public static Date dateFromString(final String string_, final String format_)
  {
    if (string_ != null && !string_.trim().isEmpty())
    {
      try
      {
        return new SimpleDateFormat(format_).parse(string_);
      }
      catch (final ParseException ex_)
      {
        //ignore
      }
    }
    return null;
  }

  public static boolean hasInvalidIntegers(final String... str_)
  {
    for (final String str : str_)
    {
      if (null == integerFromString(str))
      {
        return true;
      }
    }
    return false;
  }

  public static boolean hasNullsorEmpties(final String... str_)
  {
    for (final String str : str_)
    {
      if (null == str || str.trim().isEmpty())
      {
        return true;
      }
    }
    return false;
  }

  public static Integer integerFromString(final String string_)
  {
    if (string_ != null && !string_.trim().isEmpty())
    {
      try
      {
        return Integer.parseInt(string_.trim());
      }
      catch (final NumberFormatException ex_)
      {
        //ignore
      }
    }
    return null;
  }

  public static String leftPad(final String str_)
  {
    if (str_ != null)
    {
      return String.format("%04d", Integer.parseInt(str_));
    }
    return null;
  }
}
