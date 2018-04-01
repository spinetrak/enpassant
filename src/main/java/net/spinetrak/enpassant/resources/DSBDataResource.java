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

import net.spinetrak.enpassant.core.dsb.dtos.DSBAssociationTree;
import net.spinetrak.enpassant.core.dsb.dtos.DSBStats;
import net.spinetrak.enpassant.core.dsb.pojos.DSBAssociation;
import net.spinetrak.enpassant.core.dsb.pojos.DSBClub;
import net.spinetrak.enpassant.core.dsb.pojos.DSBPlayer;
import net.spinetrak.enpassant.db.DSBDataCache;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/dsb")
@Produces(MediaType.APPLICATION_JSON)
public class DSBDataResource
{
  private final DSBDataCache _dsbDataCache;

  public DSBDataResource(final DSBDataCache dsbDataCache_)
  {
    _dsbDataCache = dsbDataCache_;
  }

  @Path("/association/{associationId}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public DSBAssociation getAssociation(@PathParam("associationId") final String associationId_)
  {
    final DSBAssociation dsbAssociation = _dsbDataCache.getDSBAssociation(associationId_);
    if (null != dsbAssociation)
    {
      return dsbAssociation;
    }
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }

  @Path("/associationTree/{associationId}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public DSBAssociationTree getAssociationTree(@PathParam("associationId") final String associationId_)
  {
    final DSBAssociation dsbAssociation = _dsbDataCache.getDSBAssociation(associationId_);
    if (null != dsbAssociation)
    {
      final DSBAssociationTree dsbAssociationTree = new DSBAssociationTree();
      dsbAssociationTree.setDsbAssociation(dsbAssociation);
      return dsbAssociationTree;
    }
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }

  @Path("/club/{clubId}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public DSBClub getClub(@PathParam("clubId") final String clubId_)
  {
    final DSBClub club = _dsbDataCache.getDSBClub(clubId_);
    if (club != null)
    {
      return club;
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

  @Path("/players/{clubOrAssociationId_}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public List<DSBPlayer> getPlayers(@PathParam("clubOrAssociationId_") final String clubOrAssociationId_)
  {
    final List<DSBPlayer> players = _dsbDataCache.getDSBPlayers(clubOrAssociationId_);
    if (players.isEmpty())
    {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
    return players;
  }

  @Path("/stats/{associationId}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public List<DSBStats> getStats(@PathParam("associationId") final String clubOrAssociationId_)
  {
    final Map<Integer, Float[]> finalResults = _dsbDataCache.getStats(clubOrAssociationId_);

    final DSBStats dsbStats = new DSBStats();
    dsbStats.setStats(finalResults);

    return dsbStats.getData();
  }
}
