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

import net.spinetrak.enpassant.core.dsb.pojos.DSBSpieler;
import net.spinetrak.enpassant.core.dsb.pojos.DSBVerband;
import net.spinetrak.enpassant.core.dsb.pojos.DSBVerein;
import net.spinetrak.enpassant.core.dsb.pojos.DWZ;
import net.spinetrak.enpassant.core.fide.FIDE;
import net.spinetrak.enpassant.core.utils.Converters;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DSBDataTransformer
{
  private final static Logger LOGGER = LoggerFactory.getLogger(DSBDataTransformer.class);

  public static DSBVerband createDSBVerbandFromZIPFile(final String dataFile_)
  {
    LOGGER.info("Creating DSB Verband");
    final DSBVerband dsb = new DSBVerband("00000", null, DSBVerband.BUND, "Deutscher Schachbund");
    final List<Spieler> spielers = new ArrayList<>();
    final List<Verein> vereine = new ArrayList<>();
    ZipInputStream zipIn;
    try
    {
      LOGGER.info("Processing ZIP file " + dataFile_);
      zipIn = new ZipInputStream(new FileInputStream(new File(dataFile_)));
      ZipEntry entry;
      while ((entry = zipIn.getNextEntry()) != null)
      {
        final String csvFileName = entry.getName();
        LOGGER.info("Found ZIP Entry: " + csvFileName);
        if (!csvFileName.endsWith("csv"))
        {
          continue;
        }
        InputStreamReader isr;
        try
        {
          isr = new InputStreamReader(zipIn, Charset.forName("Cp1252"));
          final CSVParser csv = new CSVParser(isr, CSVFormat.DEFAULT);
          final List<CSVRecord> records = csv.getRecords();
          LOGGER.info("Found records: " + records.size());

          if ("verbaende.csv".equalsIgnoreCase(csvFileName))
          {
            updateDSBVerbaende(dsb, records);
          }
          else if ("vereine.csv".equalsIgnoreCase(csvFileName))
          {
            updateDSBVereine(vereine, records);
          }
          else if ("spieler.csv".equalsIgnoreCase(csvFileName))
          {
            updateDSBPlayers(spielers, records);
          }
        }
        catch (final IOException ex_)
        {
          LOGGER.error("Error processing ZIP entry", ex_);
        }
      }
    }
    catch (final Exception ex_)
    {
      LOGGER.error("Error processing ZIP file", ex_);
    }
    for (final Verein verein : vereine)
    {
      final String verbandsID = verein.getVerband();
      final DSBVerband verband = dsb.getVerband(verbandsID);
      verband.add(new DSBVerein(verein.getID(), verein.getName(), verband.getId()));
    }
    LOGGER.info("Done adding vereine.");

    for (final Spieler spieler : spielers)
    {

      final String spielerVerein = spieler.getVereinsID();
      final DSBVerein verein = dsb.getVerein(spielerVerein);
      if (null == verein)
      {
        final DSBVerband verband = dsb.getVerband(spielerVerein);
        if (null != verband)
        {
          final DSBVerein vereinslos = verband.asVerein();
          final DSBSpieler dsbSpieler = new DSBSpieler(vereinslos.getId(), spieler.getID(), spieler.getName(),
                                                       spieler.getStatus(),
                                                       spieler.getGender(),
                                                       spieler.getEligibility(), spieler.getYOB(), spieler.getDWZ(),
                                                       spieler.getFIDE());
          vereinslos.add(dsbSpieler);
        }
        continue;
      }
      final DSBSpieler dsbSpieler = new DSBSpieler(verein.getId(), spieler.getID(), spieler.getName(),
                                                   spieler.getStatus(),
                                                   spieler.getGender(),
                                                   spieler.getEligibility(), spieler.getYOB(), spieler.getDWZ(),
                                                   spieler.getFIDE());
      verein.add(dsbSpieler);

    }
    LOGGER.info("Done adding spieler.");
    return dsb;
  }

  private static void updateDSBPlayers(final List<Spieler> spieler_s,
                                       final List<CSVRecord> records_)
  {
    for (final CSVRecord record : records_)
    {
      if ("ZPS".equalsIgnoreCase(record.get(0).trim()))
      {
        continue;
      }
      final Spieler spieler = new Spieler(record.get(0), record.get(1), record.get(2), record.get(3), record.get(4),
                                          record.get(5), record.get(6), record.get(7),
                                          record.get(8), record.get(9), record.get(10), record.get(11),
                                          record.get(12), record.get(13));
      spieler_s.add(spieler);
    }
  }

  private static void updateDSBVerbaende(final DSBVerband dsb_, final List<CSVRecord> records_)
  {
    final List<Verband> verbaende = new ArrayList<>();
    for (final CSVRecord record : records_)
    {
      if ("Verband".equalsIgnoreCase(record.get(0).trim()))
      {
        continue;
      }
      final Verband verband = new Verband(record.get(0), record.get(1), record.get(2), record.get(3));
      verbaende.add(verband);
    }

    //first pass for DSBVerband.LAND
    for (final Verband verband : verbaende)
    {
      final String parent = verband.getParent();
      final String id = verband.getId() + "00";
      if (parent != null && "000".equals(parent.trim()))
      {
        final String parentId = "00000";
        dsb_.add(new DSBVerband(id, parentId, DSBVerband.LAND, verband.getName()));
      }
    }


    //second pass for DSBVerband.BEZIRK
    for (final Verband verband : verbaende)
    {
      final String parent = verband.getParent();
      final String id = verband.getId() + "00";
      if (parent != null)
      {
        final String parentId = parent.trim() + "00";
        if ("00000".equals(parentId))
        {
          continue;
        }
        if ('0' == (parentId.charAt(1)))
        {
          final int level = DSBVerband.BEZIRK;
          final DSBVerband dsbVerband = dsb_.getVerband(parentId);
          if (dsbVerband != null)
          {
            dsbVerband.add(
              new DSBVerband(id, parentId, level, verband.getName()));
          }
          else
          {
            LOGGER.error("No BEZIRK association found for id " + parentId);
          }
        }
      }
    }

    //third pass for DSBVerband.KREIS
    for (final Verband verband : verbaende)
    {
      final String parent = verband.getParent();
      final String id = verband.getId() + "00";
      if (parent != null)
      {
        final String parentId = parent.trim() + "00";
        if ('0' != (parentId.charAt(1)))
        {
          final int level = DSBVerband.KREIS;
          final DSBVerband dsbVerband = dsb_.getVerband(parentId);
          dsbVerband.add(new DSBVerband(id, parentId, level, verband.getName()));
        }
      }
    }
  }

  private static void updateDSBVereine(final List<Verein> verein_s, final List<CSVRecord> records_)
  {
    for (final CSVRecord record : records_)
    {
      if ("ZPS".equalsIgnoreCase(record.get(0).trim()))
      {
        continue;
      }
      final Verein verein = new Verein(record.get(0), record.get(1) + "0000", record.get(2) + "00", record.get(3));
      verein_s.add(verein);
    }
  }

  private static class Spieler
  {
    final String _dwz;
    final String _eligibility;
    final String _fideCountry;
    final String _fideElo;
    final String _fideID;
    final String _fideTitle;
    final String _gender;
    final String _index;
    final String _lastEvaluation;
    final String _membernr;
    final String _name;
    final String _status;
    final String _yearOfBirth;
    final String _zps;

    Spieler(final String zps_, final String membernr_, final String status_, final String name_,
            final String gender_, final String eligibility_, final String yearOfBirth_,
            final String lastEvaluation_, final String dwz_, final String index_, final String fideElo_,
            final String fideTitle_, final String fideID, final String fideCountry_)
    {
      _zps = zps_;
      _membernr = membernr_;
      _status = status_;
      _name = name_;
      _gender = gender_;
      _eligibility = eligibility_;
      _yearOfBirth = yearOfBirth_;
      _lastEvaluation = lastEvaluation_;
      _dwz = dwz_;
      _index = index_;
      _fideElo = fideElo_;
      _fideTitle = fideTitle_;
      _fideID = fideID;
      _fideCountry = fideCountry_;
    }

    DWZ getDWZ()
    {
      if (Converters.noNullsorEmpties(_dwz, _index, _lastEvaluation))
      {
        return new DWZ(Converters.integerFromString(_dwz), Converters.integerFromString(_index),
                       Converters.dateFromString(_lastEvaluation, "YYYYww"));
      }
      return null;
    }

    String getEligibility()
    {
      return _eligibility;
    }

    FIDE getFIDE()
    {
      if (Converters.noNullsorEmpties(_fideID, _fideElo, _fideTitle, _fideCountry))
      {
        return new FIDE(Converters.integerFromString(_fideID),
                        Converters.integerFromString(_fideElo),
                        _fideTitle, _fideCountry);
      }
      return null;
    }

    String getGender()
    {
      if (Converters.noNullsorEmpties(_gender))
      {
        return _gender.trim();
      }
      return null;
    }

    String getID()
    {
      return _membernr;
    }

    String getStatus()
    {
      return _status;
    }

    String getVereinsID()
    {
      return _zps;
    }

    Integer getYOB()
    {
      return Converters.integerFromString(_yearOfBirth);
    }

    public String getName()
    {
      return _name;
    }

    @Override
    public String toString()
    {
      return "Player{" +
        "zps='" + _zps + '\'' +
        ", membernr='" + _membernr + '\'' +
        ", status='" + _status + '\'' +
        ", name='" + _name + '\'' +
        ", gender='" + _gender + '\'' +
        ", eligibility='" + _eligibility + '\'' +
        ", yearOfBirth='" + _yearOfBirth + '\'' +
        ", lastEvaluation='" + _lastEvaluation + '\'' +
        ", dwz='" + _dwz + '\'' +
        ", index='" + _index + '\'' +
        ", fideElo='" + _fideElo + '\'' +
        ", fideTitle='" + _fideTitle + '\'' +
        ", fideID='" + _fideID + '\'' +
        ", fideCountry='" + _fideCountry + '\'' +
        '}';
    }
  }

  private static class Verein
  {
    private final String _landesverband;
    private final String _name;
    private final String _verband;
    private final String _zps;

    Verein(final String zps_, final String landesverband_, final String verband_, final String name_)
    {
      _verband = verband_;
      _landesverband = landesverband_;
      _zps = zps_;
      _name = name_;
    }

    String getID()
    {
      return _zps;
    }

    String getVerband()
    {
      return _verband;
    }

    public String getName()
    {
      return _name;
    }

    @Override
    public String toString()
    {
      return "Club{" +
        "landesverband='" + _landesverband + '\'' +
        ", name='" + _name + '\'' +
        ", zps='" + _zps + '\'' +
        ", verband='" + _verband + '\'' +
        '}';
    }
  }

  private static class Verband
  {
    private final String _id;
    private final String _name;
    private final String _parent;
    private final String _region;

    Verband(final String id_, final String region_, final String parentverband_,
            final String name_)
    {
      _id = id_;
      _region = region_;
      _parent = parentverband_;
      _name = name_;
    }

    String getId()
    {
      return _id;
    }

    String getParent()
    {
      return _parent;
    }

    public String getName()
    {
      return _name;
    }

    public String getRegion()
    {
      return _region;
    }

    @Override
    public String toString()
    {
      return "Association{" +
        "region='" + _region + '\'' +
        ", name='" + _name + '\'' +
        ", parent='" + _parent + '\'' +
        ", id='" + _id + '\'' +
        '}';
    }
  }
}
