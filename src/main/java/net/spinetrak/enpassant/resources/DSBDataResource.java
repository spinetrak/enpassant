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

import net.spinetrak.enpassant.core.dsb.daos.DSBSpielerDAO;
import net.spinetrak.enpassant.core.dsb.daos.DSBVerbandDAO;
import net.spinetrak.enpassant.core.dsb.daos.DSBVereinDAO;
import net.spinetrak.enpassant.core.dsb.pojos.DSBVerband;
import net.spinetrak.enpassant.core.dsb.pojos.DSBVerein;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/dsb")
@Produces(MediaType.APPLICATION_JSON)
public class DSBDataResource
{
  private final DSBSpielerDAO _dsbSpielerDAO;
  private final DSBVerbandDAO _dsbVerbandDAO;
  private final DSBVereinDAO _dsbVereinDAO;

  public DSBDataResource(final DSBVerbandDAO dsbVerbandDAO_, final DSBVereinDAO dsbVereinDAO_,
                         final DSBSpielerDAO dsbSpielerDAO_)
  {
    _dsbSpielerDAO = dsbSpielerDAO_;
    _dsbVerbandDAO = dsbVerbandDAO_;
    _dsbVereinDAO = dsbVereinDAO_;
  }

  @Path("/verband/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public DSBVerband getVerband(@PathParam("id") @DefaultValue("00000") String id_)
  {
    final List<DSBVerband> verbaende = _dsbVerbandDAO.select(id_);
    if (!verbaende.isEmpty())
    {
      final DSBVerband dsbVerband = verbaende.get(0);

      for (final DSBVerband child : getChildren(dsbVerband))
      {
        dsbVerband.add(child);
      }
      return dsbVerband;
    }
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }

  @Path("/verein/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public DSBVerein getVerein(@PathParam("id") @DefaultValue("00000") String id_)
  {
    final List<DSBVerein> vereine = _dsbVereinDAO.select(id_);
    if (!vereine.isEmpty())
    {
      final DSBVerein verein = vereine.get(0);
      verein.add(_dsbSpielerDAO.selectPlayers(verein.getId()));
      return verein;
    }
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }

  /**
   * recursive lookup
   **/
  private List<DSBVerband> getChildren(final DSBVerband verband_)
  {
    final List<DSBVerband> verbaende = _dsbVerbandDAO.selectChildrenOf(verband_.getId());
    for (final DSBVerband verband : verbaende)
    {
      getChildren(verband);
      verband_.add(verband);
    }

    final List<DSBVerein> vereine = _dsbVereinDAO.selectChildrenOf(verband_.getId());
    for (final DSBVerein verein : vereine)
    {
      verein.add(_dsbSpielerDAO.selectPlayers(verein.getId()));
      verband_.add(verein);
    }
    return verbaende;
  }

}
