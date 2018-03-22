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
import net.spinetrak.enpassant.core.dsb.daos.DSBSpielerDAO;
import net.spinetrak.enpassant.core.dsb.daos.DSBVerbandDAO;
import net.spinetrak.enpassant.core.dsb.daos.DSBVereinDAO;
import net.spinetrak.enpassant.core.dsb.pojos.DSBSpieler;
import net.spinetrak.enpassant.core.dsb.pojos.DSBVerband;
import net.spinetrak.enpassant.core.dsb.pojos.DSBVerein;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DSBDataUpdater implements Runnable
{
  private final static Logger LOGGER = LoggerFactory.getLogger(DSBDataUpdater.class);
  private final DSBSpielerDAO _dsbSpielerDAO;
  private final DSBVerbandDAO _dsbVerbandDAO;
  private final DSBVereinDAO _dsbVereinDAO;
  private DSBZipFileProcessor _dsbZipFileProcessor;

  public DSBDataUpdater(final DSBVerbandDAO dsbVerbandDAO_, final DSBVereinDAO dsbVereinDAO_,
                        final DSBSpielerDAO dsbSpielerDAO_, final DSBZipFileProcessor dsbZipFileProcessor_)
  {
    _dsbVerbandDAO = dsbVerbandDAO_;
    _dsbVereinDAO = dsbVereinDAO_;
    _dsbSpielerDAO = dsbSpielerDAO_;
    _dsbZipFileProcessor = dsbZipFileProcessor_;
  }


  @Override
  public void run()
  {
    updateDatabase(_dsbZipFileProcessor.getDSBVerband());
  }

  private void updateDatabase(final DSBVerband dsbVerband_)
  {
    LOGGER.info("Upserting verband: " + dsbVerband_.getName());
    _dsbVerbandDAO.insertOrUpdate(dsbVerband_);
    for (final DSBVerein verein : dsbVerband_.getVereine().values())
    {
      _dsbVereinDAO.insertOrUpdate(verein);
      for (final DSBSpieler spieler : verein.getSpieler())
      {
        _dsbSpielerDAO.insertOrUpdateSpieler(spieler);
        if (spieler.getDwz() != null)
        {
          _dsbSpielerDAO.insertOrUpdateDWZ(spieler);
          if (spieler.getFide() != null)
          {
            _dsbSpielerDAO.insertOrUpdateFIDE(spieler);
          }
        }
      }
    }
    for (final DSBVerband verband : dsbVerband_.getVerbaende().values())
    {
      updateDatabase(verband);
    }
  }
}
