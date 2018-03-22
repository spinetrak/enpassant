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

package net.spinetrak.enpassant.resources;

import net.spinetrak.enpassant.configuration.DSBZipFileProcessor;
import net.spinetrak.enpassant.core.dsb.daos.DSBSpielerDAO;
import net.spinetrak.enpassant.core.dsb.daos.DSBVerbandDAO;
import net.spinetrak.enpassant.core.dsb.daos.DSBVereinDAO;
import net.spinetrak.enpassant.core.dsb.pojos.DSBVerband;
import net.spinetrak.enpassant.core.dsb.pojos.DSBVerein;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Path("/dsb")
@Produces(MediaType.APPLICATION_JSON)
public class DSBDataResource
{
  private final static Logger LOGGER = LoggerFactory.getLogger(DSBDataResource.class);
  private final DSBSpielerDAO _dsbSpielerDAO;
  private final DSBVerbandDAO _dsbVerbandDAO;
  private final DSBVereinDAO _dsbVereinDAO;
  private final DSBZipFileProcessor _dsbZipFileProcessor;
  private DSBVerband _dsbVerband = null;

  public DSBDataResource(final DSBVerbandDAO dsbVerbandDAO_, final DSBVereinDAO dsbVereinDAO_,
                         final DSBSpielerDAO dsbSpielerDAO_, final DSBZipFileProcessor dsbZipFileProcessor_)
  {
    _dsbZipFileProcessor = dsbZipFileProcessor_;
    _dsbSpielerDAO = dsbSpielerDAO_;
    _dsbVerbandDAO = dsbVerbandDAO_;
    _dsbVereinDAO = dsbVereinDAO_;
  }

  @Path("/verband/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public DSBVerband getVerband(@PathParam("id") @DefaultValue("00000") String id_)
  {
    if (_dsbVerband != null)
    {
      final DSBVerband verband = _dsbVerband.getVerband(id_);

      if (verband != null)
      {
        return verband;
      }
    }
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }

  @Path("/verein/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public DSBVerein getVerein(@PathParam("id") @DefaultValue("00000") String id_)
  {
    if (_dsbVerband != null)
    {
      final DSBVerein verein = _dsbVerband.getVerein(id_);

      if (verein != null)
      {
        return verein;
      }
    }
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }

  public boolean isUpToDate()
  {
    return _dsbZipFileProcessor.isUpToDate();
  }

  public Date lastUpdate()
  {
    return _dsbZipFileProcessor.lastUpdate();
  }

}
