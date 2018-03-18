package net.spinetrak.enpassant.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.spinetrak.enpassant.core.dsb.DSBVerband;
import net.spinetrak.enpassant.core.dsb.etl.DSBDataTransformer;
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

public class DSBDataClient implements Runnable
{
  private final static String DSB_DATA_FILE = System.getProperty("java.io.tmpdir") + "dsb_data.zip";
  private final static String DSB_JSON_FILE = System.getProperty("java.io.tmpdir") + "dsb_data.json";
  private final static Logger LOGGER = LoggerFactory.getLogger(DSBDataClient.class);
  private final String _url;
  private final Object _lock = new Object();

  public DSBVerband getDSBVerband()
  {
    synchronized (_lock)
    {
      while (_dsb == null)
      {
        try
        {
          LOGGER.warn("Waiting dor data ...");
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

  private DSBVerband _dsb;
  private Date _lastUpdate;


  public DSBDataClient(final String url_)
  {
    _url = url_;
  }

  public boolean isUpToDate()
  {
    return zipFileIsCurrent() && _dsb != null && _lastUpdate != null && _dsb.getVerbaende().size() > 10;
  }

  public Date lastUpdate()
  {
    return _lastUpdate;
  }


  @Override
  public void run()
  {
    if (!zipFileIsCurrent())
    {
      LOGGER.info("Downloading " + _url);
      downloadZipFile();
    }
    _dsb = DSBDataTransformer.createDSBVerbandFromZIPFile(DSB_DATA_FILE);
    final ObjectMapper mapper = new ObjectMapper();
    try
    {
      mapper.writeValue(new File(DSB_JSON_FILE), _dsb);
      LOGGER.info("Done converting to JSON: " + DSB_JSON_FILE);
    }
    catch (final JsonProcessingException ex_)
    {
      LOGGER.error("Error converting to JSON: " + ex_);
    }
    catch (final IOException ex_)
    {
      LOGGER.error("Error writing to JSON: " + ex_);
    }
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
