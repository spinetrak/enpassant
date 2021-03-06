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

import io.dropwizard.jersey.params.BooleanParam;
import net.spinetrak.enpassant.core.dsb.dtos.DSBOrganizationTree;
import net.spinetrak.enpassant.core.dsb.dtos.DSBPlayerStats;
import net.spinetrak.enpassant.core.dsb.dtos.DSBStats;
import net.spinetrak.enpassant.core.dsb.pojos.DSBOrganization;
import net.spinetrak.enpassant.core.dsb.pojos.DSBPlayer;
import net.spinetrak.enpassant.db.DSBDataCache;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/dsb")
@Produces(MediaType.APPLICATION_JSON)
public class DSBDataResource
{
  private final DSBDataCache _dsbDataCache;

  public DSBDataResource(final DSBDataCache dsbDataCache_)
  {
    _dsbDataCache = dsbDataCache_;
  }

  @Path("/organization/{organizationId}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public DSBOrganization getOrganization(@PathParam("organizationId") final String organizationId_)
  {
    final DSBOrganization dsbOrganization = _dsbDataCache.getDSBOrganization(organizationId_);
    if (null != dsbOrganization)
    {
      return dsbOrganization;
    }
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }

  @Path("/organizationTree/{organizationId}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public DSBOrganizationTree getOrganizationTree(@PathParam("organizationId") final String organizationId_)
  {
    final DSBOrganization dsbOrganization = _dsbDataCache.getDSBOrganization(organizationId_);
    if (null != dsbOrganization)
    {
      final DSBOrganizationTree dsbOrganizationTree = new DSBOrganizationTree();
      dsbOrganizationTree.setDsbOrganization(dsbOrganization);
      return dsbOrganizationTree;
    }
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }

  @Path("/player/{playerId}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public DSBPlayer getPlayer(@PathParam("playerId") final String playerId_)
  {

    final DSBPlayer player = _dsbDataCache.getDSBPlayer(playerId_);
    if (player != null)
    {
      return player;
    }

    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }

  @Path("/playerStats/{playerId}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public List<DSBPlayerStats> getPlayerStats(@PathParam("playerId") final String playerId_)
  {
    final List<DSBPlayerStats> stats = _dsbDataCache.getDSBPlayerStats(playerId_);

    if (stats != null && !stats.isEmpty())
    {
      return stats;
    }
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }

  @Path("/players/{organizationId_}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public List<DSBPlayer> getPlayers(@PathParam("organizationId_") final String organizationId_, @QueryParam("details") @DefaultValue("false") BooleanParam details_)
  {
    final List<DSBPlayer> players = _dsbDataCache.getDSBPlayers(organizationId_);
    if (players.isEmpty())
    {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    final boolean details = details_.get();
    if(!details)
    {
      for(final DSBPlayer player : players)
      {
        player.setDWZ(null);
        player.setFIDE(null);
      }
    }
    return players;
  }

  @Path("/players/html/{organizationId_}")
  @Produces(MediaType.TEXT_HTML)
  @GET
  public Response getPlayersAsHTML(@PathParam("organizationId_") final String organizationId_, @QueryParam("details") @DefaultValue("false") BooleanParam details_)
  {
    final List<DSBPlayer> players = _dsbDataCache.getDSBPlayers(organizationId_);
    if (players.isEmpty())
    {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    final boolean details = details_.get();
    if(!details)
    {
      for(final DSBPlayer player : players)
      {
        player.setDWZ(null);
        player.setFIDE(null);
      }
    }
    return Response
      .ok()
      .entity(players)
      .build();
  }

  @Path("/stats/{organizationId}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public List<DSBStats> getStats(@PathParam("organizationId") final String organizationId_)
  {
    return _dsbDataCache.getStats(organizationId_);
  }
}
