package net.spinetrak.enpassant.resources;

import net.spinetrak.enpassant.core.dsb.DSBVerband;
import net.spinetrak.enpassant.core.dsb.DSBVerein;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/dsb")
@Produces(MediaType.APPLICATION_JSON)
public class DSBDataResource
{
  final private DSBVerband _dsbVerband;

  public DSBDataResource(final DSBVerband dsbVerband_)
  {
    _dsbVerband = dsbVerband_;
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
    final DSBVerein verein = _dsbVerband.getVerein(id_);

    if (verein != null)
    {
      return verein;
    }
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }
}
