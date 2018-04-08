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

package net.spinetrak.enpassant.core.dsb.etl;

import net.spinetrak.enpassant.core.utils.Converters;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DSBCSVFileDataTransformer
{
  private final static Logger LOGGER = LoggerFactory.getLogger(DSBCSVFileDataTransformer.class);

  public Map<String, Integer> getZPStoDSBIDMapping(final String club_)
  {
    final Map<String, Integer> mapping = new HashMap<>();
    try
    {
      final CSVParser csv = CSVParser.parse(
        new URL("https://www.schachbund.de/php/dewis/verein.php?zps=" + club_ + "&format=csv"),
        Charset.forName("Cp1252"), CSVFormat.DEFAULT.withDelimiter('|'));
      final List<CSVRecord> records = csv.getRecords();
      mapping.putAll(processRecords(records));
      return mapping;
    }
    catch (final IOException ex_)
    {
      LOGGER.error("Error retrieving players for club " + club_);
    }
    catch (final DSBCSVFileParseException ex_)
    {
      LOGGER.error("Error parsing CSV file for club " + club_ + " due to " + ex_.getMessage());
    }
    return mapping;
  }

  private Map<String, Integer> processRecords(final List<CSVRecord> records_) throws DSBCSVFileParseException
  {
    final Map<String, Integer> mapping = new HashMap<>();
    for (final CSVRecord record : records_)
    {
      if ("id".equalsIgnoreCase(record.get(0).trim()))
      {
        continue;
      }
      try
      {
        if (record.size() >= 6)
        {
          mapping.put(record.get(4) + "-" + Converters.leftPad(record.get(5)), Integer.parseInt(record.get(0)));
        }
        else
        {
          throw new IllegalStateException("Invalid record: " + record.get(0));
        }
      }
      catch (final Exception ex_)
      {
        throw new DSBCSVFileParseException(
          "Error reading record " + record.toString() + " due to " + ex_.getMessage());
      }
    }
    return mapping;
  }

  private static class DSBCSVFileParseException extends Exception
  {
    DSBCSVFileParseException(final String str_)
    {
      super(str_);
    }
  }
}
