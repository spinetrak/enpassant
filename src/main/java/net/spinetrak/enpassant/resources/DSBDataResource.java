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

import net.spinetrak.enpassant.core.dsb.pojos.DSBVerband;
import net.spinetrak.enpassant.core.dsb.pojos.DSBVerein;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/dsb")
@Produces(MediaType.APPLICATION_JSON)
public class DSBDataResource
{
  private final static Logger LOGGER = LoggerFactory.getLogger(DSBDataResource.class);
  final private DSBVerband _dsbVerband;
  final private Jdbi _jdbi;

  public DSBDataResource(final DSBVerband dsbVerband_, final Jdbi jdbi_)
  {
    _dsbVerband = dsbVerband_;
    _jdbi = jdbi_;
  }

  @Path("/verband/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public DSBVerband getVerband(@PathParam("id") @DefaultValue("00000") String id_)
  {
    final DSBVerband verband = _dsbVerband.getVerband(id_);

    if (verband != null)
    {
      return verband;
    }
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }

  @Path("/verein/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public DSBVerein getVerein(@PathParam("id") @DefaultValue("00000") String id_)
  {
    LOGGER.info("Finding verein " + id_);
    final DSBVerein verein = _dsbVerband.getVerein(id_);

    if (verein != null)
    {
      return verein;
    }
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }
}
