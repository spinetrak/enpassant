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

package net.spinetrak.enpassant.configuration;

import net.spinetrak.enpassant.core.dsb.etl.DSBZIPFileDataTransformer;
import net.spinetrak.enpassant.core.dsb.pojos.DSBOrganization;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;

public class DSBZipFileProcessor implements Runnable
{
  private final static String DSB_DATA_FILE = System.getProperty("java.io.tmpdir") + File.separator + "dsb_data.zip";
  private final static Logger LOGGER = LoggerFactory.getLogger(DSBZipFileProcessor.class);
  private final Object _lock = new Object();
  private final String _url;
  private DSBOrganization _dsb;
  private Date _lastUpdate;

  DSBZipFileProcessor(final String url_)
  {
    _url = url_;
  }

  public DSBOrganization getDSBOrganization()
  {
    synchronized (_lock)
    {
      while (_dsb == null)
      {
        try
        {
          LOGGER.warn("Waiting for data ...");
          _lock.wait(1000);
        }
        catch (final InterruptedException ex_)
        {
          LOGGER.error("Error waiting dor data: " + ex_);
        }
      }
    }
    return _dsb;
  }

  public boolean isUpToDate()
  {
    return zipFileIsCurrent() && _dsb != null && _lastUpdate != null && _dsb.getOrganizations().size() > 10;
  }

  public Date lastUpdate()
  {
    return new Date(_lastUpdate.getTime());
  }


  @Override
  public void run()
  {
    if (!zipFileIsCurrent())
    {
      LOGGER.info("Downloading " + _url);
      downloadZipFile();
    }
    _dsb = DSBZIPFileDataTransformer.createDSBOrganizationFromZIPFile(DSB_DATA_FILE);

    _lastUpdate = new Date();
  }

  private void downloadZipFile()
  {
    try (
      final ReadableByteChannel in = Channels.newChannel(new URL(_url).openStream());
      final FileChannel out = new FileOutputStream(DSB_DATA_FILE).getChannel())
    {
      out.transferFrom(in, 0, Long.MAX_VALUE);
    }
    catch (final IOException ex_)
    {
      LOGGER.error("Error downloading ZIP file", ex_);
    }
    LOGGER.info("Done downloading ZIP file to " + DSB_DATA_FILE);
  }

  private boolean zipFileIsCurrent()
  {
    final File zipFile = new File(DSB_DATA_FILE);
    final DateTime now = new DateTime();
    final DateTime file = new DateTime(zipFile.lastModified());
    final Duration duration = new Duration(file, now);
    final long ageInHours = duration.getStandardHours();
    return zipFile.exists() && zipFile.canRead() && (ageInHours < 24);
  }

}
