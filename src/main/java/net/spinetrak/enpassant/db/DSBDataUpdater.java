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

package net.spinetrak.enpassant.db;

import net.spinetrak.enpassant.configuration.DSBZipFileProcessor;
import net.spinetrak.enpassant.core.dsb.daos.DSBOrganizationDAO;
import net.spinetrak.enpassant.core.dsb.daos.DSBPlayerDAO;
import net.spinetrak.enpassant.core.dsb.etl.DSBCSVFileDataTransformer;
import net.spinetrak.enpassant.core.dsb.pojos.DSBOrganization;
import net.spinetrak.enpassant.core.dsb.pojos.DSBPlayer;
import net.spinetrak.enpassant.core.dsb.pojos.DWZ;
import net.spinetrak.enpassant.core.fide.FIDE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DSBDataUpdater implements Runnable
{
  private final static Logger LOGGER = LoggerFactory.getLogger(DSBDataUpdater.class);
  private final DSBOrganizationDAO _dsbOrganizationDAO;
  private final DSBPlayerDAO _dsbPlayerDAO;
  private DSBZipFileProcessor _dsbZipFileProcessor;

  public DSBDataUpdater(final DSBOrganizationDAO dsbOrganizationDAO_,
                        final DSBPlayerDAO dsbPlayerDAO_, final DSBZipFileProcessor dsbZipFileProcessor_)
  {
    _dsbOrganizationDAO = dsbOrganizationDAO_;
    _dsbPlayerDAO = dsbPlayerDAO_;
    _dsbZipFileProcessor = dsbZipFileProcessor_;
  }


  @Override
  public void run()
  {
    updateDatabase(_dsbZipFileProcessor.getDSBOrganization());
  }

  private void updateDatabase(final DSBOrganization dsbOrganization_)
  {
    try
    {
      _dsbOrganizationDAO.insertOrUpdate(dsbOrganization_);

      if (dsbOrganization_.getIsClub())
      {
        final Map<String, Integer> zpsToDSBIdMapping = new DSBCSVFileDataTransformer().getZPStoDSBIDMapping(
          dsbOrganization_.getOrganizationId());
        for (final DSBPlayer player : dsbOrganization_.getPlayers())
        {
          final Integer dsbid = zpsToDSBIdMapping.get(
            player.getClubId() + "-" + player.getMemberId());
          if (dsbid != null)
          {
            player.setDsbId(dsbid);
          }
          _dsbPlayerDAO.insertOrUpdatePlayer(player);

          if (player.getDWZ() != null)
          {
            for (final DWZ dwz : player.getDWZ())
            {
              if (dwz != null)
              {
                _dsbPlayerDAO.insertOrUpdateDWZ(dwz);
              }
            }
          }
          if (player.getFIDE() != null)
          {
            for (final FIDE fide : player.getFIDE())
            {
              try
              {
                if (fide != null)
                {
                  _dsbPlayerDAO.insertOrUpdateFIDE(fide);
                }
              }
              catch (final Exception ex_)
              {
                LOGGER.error("Error updating FIDE for player " + player + " with FIDE " + fide, ex_);
              }
            }
          }
        }
      }
      if (!dsbOrganization_.getIsClub() && dsbOrganization_.getLevel() == DSBOrganization.LAND)
      {
        LOGGER.info("Upserting organization: " + dsbOrganization_.getName());
      }

      for (final DSBOrganization organization : dsbOrganization_.getOrganizations().values())
      {
        updateDatabase(organization);
      }
    }
    catch (final Exception ex_)
    {
      LOGGER.error("Error updating database.", ex_);
    }
  }
}
