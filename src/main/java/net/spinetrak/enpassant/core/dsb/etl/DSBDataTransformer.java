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

import net.spinetrak.enpassant.core.dsb.pojos.DSBAssociation;
import net.spinetrak.enpassant.core.dsb.pojos.DSBClub;
import net.spinetrak.enpassant.core.dsb.pojos.DSBPlayer;
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

  public static DSBAssociation createDSBAssociationFromZIPFile(final String dataFile_)
  {
    LOGGER.info("Creating DSB Association");
    final DSBAssociation dsb = new DSBAssociation("00000", null, DSBAssociation.BUND, "Deutscher Schachbund");
    final List<Player> players = new ArrayList<>();
    final List<Club> clubs = new ArrayList<>();
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
            updateDSBAssociations(dsb, records);
          }
          else if ("vereine.csv".equalsIgnoreCase(csvFileName))
          {
            updateDSBClubs(clubs, records);
          }
          else if ("spieler.csv".equalsIgnoreCase(csvFileName))
          {
            updateDSBPlayers(players, records);
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
    for (final Club club : clubs)
    {
      final String associationId = club.getAssociation();
      final DSBAssociation association = dsb.getAssociation(associationId);
      association.add(new DSBClub(club.getID(), club.getName(), association.getId()));
    }
    LOGGER.info("Done adding clubs.");

    for (final Player player : players)
    {

      final String playersClub = player.getClubId();
      final DSBClub club = dsb.getClub(playersClub);
      if (null == club)
      {
        final DSBAssociation association = dsb.getAssociation(playersClub);
        if (null != association)
        {
          final DSBClub withoutClub = association.asClub();
          final DSBPlayer dsbPlayer = new DSBPlayer(withoutClub.getId(), player.getID(), player.getName(),
                                                    player.getStatus(),
                                                    player.getGender(),
                                                    player.getEligibility(), player.getYOB(), player.getDWZ(),
                                                    player.getFIDE());
          withoutClub.add(dsbPlayer);
        }
        continue;
      }
      final DSBPlayer dsbPlayer = new DSBPlayer(club.getId(), player.getID(), player.getName(),
                                                player.getStatus(),
                                                player.getGender(),
                                                player.getEligibility(), player.getYOB(), player.getDWZ(),
                                                player.getFIDE());
      club.add(dsbPlayer);

    }
    LOGGER.info("Done adding players.");
    return dsb;
  }

  private static void updateDSBAssociations(final DSBAssociation dsb_, final List<CSVRecord> records_)
  {
    final List<Association> associations = new ArrayList<>();
    for (final CSVRecord record : records_)
    {
      if ("Verband".equalsIgnoreCase(record.get(0).trim()))
      {
        continue;
      }
      final Association association = new Association(record.get(0), record.get(1), record.get(2), record.get(3));
      associations.add(association);
    }

    //first pass for DSBAssociation.LAND
    for (final Association association : associations)
    {
      final String parent = association.getParent();
      final String id = association.getId() + "00";
      if (parent != null && "000".equals(parent.trim()))
      {
        final String parentId = "00000";
        dsb_.add(new DSBAssociation(id, parentId, DSBAssociation.LAND, association.getName()));
      }
    }


    //second pass for DSBAssociation.BEZIRK
    for (final Association association : associations)
    {
      final String parent = association.getParent();
      final String id = association.getId() + "00";
      if (parent != null)
      {
        final String parentId = parent.trim() + "00";
        if ("00000".equals(parentId))
        {
          continue;
        }
        if ('0' == (parentId.charAt(1)))
        {
          final int level = DSBAssociation.BEZIRK;
          final DSBAssociation dsbAssociation = dsb_.getAssociation(parentId);
          if (dsbAssociation != null)
          {
            dsbAssociation.add(
              new DSBAssociation(id, parentId, level, association.getName()));
          }
          else
          {
            LOGGER.error("No BEZIRK association found for id " + parentId);
          }
        }
      }
    }

    //third pass for DSBAssociation.KREIS
    for (final Association association : associations)
    {
      final String parent = association.getParent();
      final String id = association.getId() + "00";
      if (parent != null)
      {
        final String parentId = parent.trim() + "00";
        if ('0' != (parentId.charAt(1)))
        {
          final int level = DSBAssociation.KREIS;
          final DSBAssociation dsbAssociation = dsb_.getAssociation(parentId);
          dsbAssociation.add(new DSBAssociation(id, parentId, level, association.getName()));
        }
      }
    }
  }

  private static void updateDSBClubs(final List<Club> clubs_, final List<CSVRecord> records_)
  {
    for (final CSVRecord record : records_)
    {
      if ("ZPS".equalsIgnoreCase(record.get(0).trim()))
      {
        continue;
      }
      final Club club = new Club(record.get(0), record.get(1) + "0000", record.get(2) + "00", record.get(3));
      clubs_.add(club);
    }
    LOGGER.info("Added " + clubs_.size() + " clubs");
  }

  private static void updateDSBPlayers(final List<Player> players_,
                                       final List<CSVRecord> records_)
  {
    for (final CSVRecord record : records_)
    {
      if ("ZPS".equalsIgnoreCase(record.get(0).trim()))
      {
        continue;
      }
      final Player player = new Player(record.get(0), record.get(1), record.get(2), record.get(3), record.get(4),
                                       record.get(5), record.get(6), record.get(7),
                                       record.get(8), record.get(9), record.get(10), record.get(11),
                                       record.get(12), record.get(13));
      players_.add(player);
    }
    LOGGER.info("Added " + players_.size() + " players");
  }

  private static class Player
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

    Player(final String zps_, final String membernr_, final String status_, final String name_,
           final String gender_, final String eligibility_, final String yearOfBirth_,
           final String lastEvaluation_, final String dwz_, final String index_, final String fideElo_,
           final String fideTitle_, final String fideID, final String fideCountry_)
    {
      _zps = zps_;
      _membernr = leftPad(membernr_);
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

    String getClubId()
    {
      return _zps;
    }

    DWZ getDWZ()
    {
      if (Converters.noNullsorEmpties(_dwz, _index, _lastEvaluation))
      {
        return new DWZ(_zps, _membernr, Converters.integerFromString(_dwz), Converters.integerFromString(_index),
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
                        _fideTitle, _fideCountry, null);
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

    private String leftPad(final String str_)
    {
      if (str_ != null)
      {
        return String.format("%04d", Integer.parseInt(str_));
      }
      return null;
    }
  }

  private static class Club
  {
    private final String _association;
    private final String _name;
    private final String _regionalAssociation;
    private final String _zps;

    Club(final String zps_, final String regionalAssociation_, final String association_, final String name_)
    {
      _association = association_;
      _regionalAssociation = regionalAssociation_;
      _zps = zps_;
      _name = name_;
    }

    String getAssociation()
    {
      return _association;
    }

    String getID()
    {
      return _zps;
    }

    public String getName()
    {
      return _name;
    }

    @Override
    public String toString()
    {
      return "Club{" +
        "landesverband='" + _regionalAssociation + '\'' +
        ", name='" + _name + '\'' +
        ", zps='" + _zps + '\'' +
        ", verband='" + _association + '\'' +
        '}';
    }
  }

  private static class Association
  {
    private final String _id;
    private final String _name;
    private final String _parent;
    private final String _region;

    Association(final String id_, final String region_, final String parent_,
                final String name_)
    {
      _id = id_;
      _region = region_;
      _parent = parent_;
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
