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
import net.spinetrak.enpassant.core.dsb.daos.DSBAssociationDAO;
import net.spinetrak.enpassant.core.dsb.daos.DSBClubDAO;
import net.spinetrak.enpassant.core.dsb.daos.DSBPlayerDAO;
import net.spinetrak.enpassant.core.dsb.pojos.DSBAssociation;
import net.spinetrak.enpassant.core.dsb.pojos.DSBClub;
import net.spinetrak.enpassant.core.dsb.pojos.DSBPlayer;
import net.spinetrak.enpassant.core.dsb.pojos.DWZ;
import net.spinetrak.enpassant.core.fide.FIDE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DSBDataUpdater implements Runnable
{
  private final static Logger LOGGER = LoggerFactory.getLogger(DSBDataUpdater.class);
  private final DSBAssociationDAO _dsbAssociationDAO;
  private final DSBClubDAO _dsbClubDAO;
  private final DSBPlayerDAO _dsbPlayerDAO;
  private DSBZipFileProcessor _dsbZipFileProcessor;

  public DSBDataUpdater(final DSBAssociationDAO dsbAssociationDAO_, final DSBClubDAO dsbClubDAO_,
                        final DSBPlayerDAO dsbPlayerDAO_, final DSBZipFileProcessor dsbZipFileProcessor_)
  {
    _dsbAssociationDAO = dsbAssociationDAO_;
    _dsbClubDAO = dsbClubDAO_;
    _dsbPlayerDAO = dsbPlayerDAO_;
    _dsbZipFileProcessor = dsbZipFileProcessor_;
  }


  @Override
  public void run()
  {
    updateDatabase(_dsbZipFileProcessor.getDSBAssociation());
  }

  private void updateDatabase(final DSBAssociation dsbAssociation_)
  {
    LOGGER.info("Upserting association: " + dsbAssociation_.getName());
    _dsbAssociationDAO.insertOrUpdate(dsbAssociation_);
    for (final DSBClub club : dsbAssociation_.getClubs().values())
    {
      _dsbClubDAO.insertOrUpdate(club);
      for (final DSBPlayer player : club.getPlayers())
      {
        _dsbPlayerDAO.insertOrUpdatePlayer(player);
        if (player.getDWZ() != null)
        {
          for (final DWZ dwz : player.getDWZ())
          {
            _dsbPlayerDAO.insertOrUpdateDWZ(dwz);
          }
        }
        if (player.getFIDE() != null)
        {
          for (final FIDE fide : player.getFIDE())
          {
            _dsbPlayerDAO.insertOrUpdateFIDE(fide);
          }
        }
      }
    }
    for (final DSBAssociation association : dsbAssociation_.getAssociations().values())
    {
      updateDatabase(association);
    }
  }
}
