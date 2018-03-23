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

import net.spinetrak.enpassant.core.dsb.daos.DSBAssociationDAO;
import net.spinetrak.enpassant.core.dsb.daos.DSBClubDAO;
import net.spinetrak.enpassant.core.dsb.daos.DSBPlayerDAO;
import net.spinetrak.enpassant.core.dsb.pojos.DSBAssociation;
import net.spinetrak.enpassant.core.dsb.pojos.DSBClub;
import net.spinetrak.enpassant.core.dsb.pojos.DSBPlayer;
import net.spinetrak.enpassant.core.dsb.pojos.DWZ;
import net.spinetrak.enpassant.core.fide.FIDE;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/dsb")
@Produces(MediaType.APPLICATION_JSON)
public class DSBDataResource
{
  private final DSBAssociationDAO _dsbAssociationDAO;
  private final DSBClubDAO _dsbClubDAO;
  private final DSBPlayerDAO _dsbPlayerDAO;

  public DSBDataResource(final DSBAssociationDAO dsbAssociationDAO_, final DSBClubDAO dsbClubDAO_,
                         final DSBPlayerDAO dsbPlayerDAO_)
  {
    _dsbPlayerDAO = dsbPlayerDAO_;
    _dsbAssociationDAO = dsbAssociationDAO_;
    _dsbClubDAO = dsbClubDAO_;
  }

  @Path("/verband/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public DSBAssociation getAssociation(@PathParam("id") @DefaultValue("00000") String id_)
  {
    final List<DSBAssociation> associations = _dsbAssociationDAO.select(id_);
    if (!associations.isEmpty())
    {
      final DSBAssociation dsbAssociation = associations.get(0);

      for (final DSBAssociation child : getChildren(dsbAssociation))
      {
        dsbAssociation.add(child);
      }
      return dsbAssociation;
    }
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }

  @Path("/verein/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public DSBClub getClub(@PathParam("id") @DefaultValue("00000") String id_)
  {
    final List<DSBClub> clubs = _dsbClubDAO.select(id_);
    if (!clubs.isEmpty())
    {
      final DSBClub club = clubs.get(0);
      club.add(_dsbPlayerDAO.select(club.getId()));

      for (final DSBPlayer player : club.getPlayers())
      {
        player.setDWZ(_dsbPlayerDAO.selectDWZ(player));
        player.setFIDE(_dsbPlayerDAO.selectFIDE(player));
      }

      return club;
    }
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }

  @Path("/spieler/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public DSBPlayer getPlayer(@PathParam("id") @DefaultValue("00000-0000") String id_)
  {
    if (id_.contains("-"))
    {
      final String[] ids = id_.split("-");
      final List<DSBPlayer> players = _dsbPlayerDAO.select(ids[0], ids[1]);
      if (!players.isEmpty())
      {
        final DSBPlayer player = players.get(0);
        final List<DWZ> dwzs = _dsbPlayerDAO.selectDWZ(player);
        player.setDWZ(dwzs);

        final List<FIDE> fides = _dsbPlayerDAO.selectFIDE(player);
        player.setFIDE(fides);
        return player;
      }
    }
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }

  /**
   * recursive lookup
   **/
  private List<DSBAssociation> getChildren(final DSBAssociation association_)
  {
    final List<DSBAssociation> associations = _dsbAssociationDAO.selectChildrenOf(association_.getId());
    for (final DSBAssociation association : associations)
    {
      getChildren(association);
      association_.add(association);
    }

    final List<DSBClub> clubs = _dsbClubDAO.selectChildrenOf(association_.getId());
    for (final DSBClub club : clubs)
    {
      club.add(_dsbPlayerDAO.select(club.getId()));
      association_.add(club);
    }
    return associations;
  }

}
