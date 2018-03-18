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

  public static boolean noNullsorEmpties(final String... str_)
  {
    for (final String str : str_)
    {
      if (null == str || str.trim().isEmpty())
      {
        return false;
      }
    }
    return true;
  }
}
