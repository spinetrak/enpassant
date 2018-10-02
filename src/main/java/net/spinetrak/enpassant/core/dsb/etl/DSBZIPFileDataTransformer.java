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

import net.spinetrak.enpassant.core.dsb.pojos.DSBOrganization;
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

public class DSBZIPFileDataTransformer
{
  private final static Logger LOGGER = LoggerFactory.getLogger(DSBZIPFileDataTransformer.class);

  public static DSBOrganization createDSBOrganizationFromZIPFile(final String dataFile_)
  {
    LOGGER.info("Creating DSB Organization");
    final DSBOrganization dsb = new DSBOrganization();
    dsb.setOrganizationId("00000");
    dsb.setParentId(null);
    dsb.setLevel(DSBOrganization.BUND);
    dsb.setName("Deutscher Schachbund");

    final List<DSBPlayer> players = new ArrayList<>();
    final List<DSBOrganization> clubs = new ArrayList<>();
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
          LOGGER.info("Found " + records.size() + " records in " + csvFileName);

          if ("verbaende.csv".equalsIgnoreCase(csvFileName))
          {
            updateDSBOrganizations(dsb, records);
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
    for (final DSBOrganization club : clubs)
    {
      final String organizationId = club.getParentId();
      final DSBOrganization organization = dsb.getOrganization(organizationId);
      organization.add(club);
    }
    LOGGER.info("Done adding clubs.");

    for (final DSBPlayer player : players)
    {
      final String playersClub = player.getClubId();
      final DSBOrganization club = dsb.getOrganization(playersClub);
      if (null == club)
      {
        continue;
      }
      player.setClubId(club.getOrganizationId());
      club.add(player);
    }
    LOGGER.info("Done adding players.");
    return dsb;
  }

  private static void updateDSBClubs(final List<DSBOrganization> clubs_, final List<CSVRecord> records_)
  {
    for (final CSVRecord record : records_)
    {
      if ("ZPS".equalsIgnoreCase(record.get(0).trim()))
      {
        continue;
      }
      final DSBOrganization club = new DSBOrganization();
      club.setIsClub(true);
      club.setOrganizationId(record.get(0));
      club.setParentId(record.get(2) + "00");
      club.setName(record.get(3));
      club.setLevel(DSBOrganization.VEREIN);
      clubs_.add(club);
    }
    LOGGER.info("Added " + clubs_.size() + " clubs");
  }

  private static void updateDSBOrganizations(final DSBOrganization dsb_, final List<CSVRecord> records_)
  {
    for (final CSVRecord record : records_)
    {
      if ("Verband".equalsIgnoreCase(record.get(0).trim()))
      {
        continue;
      }

      final DSBOrganization dsbOrganization = new DSBOrganization();
      dsbOrganization.setIsClub(false);
      dsbOrganization.setOrganizationId(record.get(0).trim() + "00");
      dsbOrganization.setName(record.get(3).trim());

      final String parentId = Converters.rightPad(record.get(2));
      dsbOrganization.setParentId(parentId);
      if ("00000".equals(parentId))
      {
        dsbOrganization.setLevel(DSBOrganization.LAND);
        dsb_.add(dsbOrganization);
      }
      else if ('0' == parentId.charAt(1))
      {
        dsbOrganization.setLevel(DSBOrganization.BEZIRK);
        final DSBOrganization land = dsb_.getOrganization(parentId);
        land.add(dsbOrganization);
      }
      else if ('0' != parentId.charAt(1))
      {
        dsbOrganization.setLevel(DSBOrganization.KREIS);
        final DSBOrganization bezirk = dsb_.getOrganization(parentId);
        bezirk.add(dsbOrganization);
      }
    }
  }

  private static void updateDSBPlayers(final List<DSBPlayer> players_,
                                       final List<CSVRecord> records_)
  {
    for (final CSVRecord record : records_)
    {
      if ("ZPS".equalsIgnoreCase(record.get(0).trim()))
      {
        continue;
      }

      final DSBPlayer dsbPlayer = new DSBPlayer();
      dsbPlayer.setClubId(record.get(0));
      dsbPlayer.setMemberId(Converters.leftPad(record.get(1)));
      dsbPlayer.setStatus(record.get(2));
      dsbPlayer.setName(record.get(3));
      dsbPlayer.setGender(record.get(4));
      dsbPlayer.setEligibility(record.get(5));
      dsbPlayer.setYoB(Converters.integerFromString(record.get(6)));
      dsbPlayer.setFideId(Converters.integerFromString(record.get(12)));

      final DWZ dwz = new DWZ();
      dwz.setClubId(dsbPlayer.getClubId());
      dwz.setMemberId(dsbPlayer.getMemberId());
      dwz.setDwz(Converters.integerFromString(record.get(8)));
      dwz.setLastEval(Converters.dateFromString(record.get(7), "YYYYww"));
      dwz.setIndex(Converters.integerFromString(record.get(9)));
      if (dwz.getDwz() != 0)
      {
        dsbPlayer.addDWZ(dwz);
      }

      final FIDE fide = new FIDE();
      fide.setId(dsbPlayer.getFideId());
      fide.setElo(Converters.integerFromString(record.get(10)));
      fide.setTitle(record.get(11));
      fide.setCountry(record.get(13));
      if (fide.getElo() != 0)
      {
        dsbPlayer.addFIDE(fide);
      }
      players_.add(dsbPlayer);
    }
    LOGGER.info("Added " + players_.size() + " players");
  }
}
