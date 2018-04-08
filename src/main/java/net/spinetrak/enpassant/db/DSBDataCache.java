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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.spinetrak.enpassant.core.dsb.daos.DSBOrganizationDAO;
import net.spinetrak.enpassant.core.dsb.daos.DSBPlayerDAO;
import net.spinetrak.enpassant.core.dsb.dtos.DSBStats;
import net.spinetrak.enpassant.core.dsb.pojos.DSBOrganization;
import net.spinetrak.enpassant.core.dsb.pojos.DSBPlayer;
import net.spinetrak.enpassant.core.dsb.pojos.DWZ;
import net.spinetrak.enpassant.core.fide.FIDE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class DSBDataCache
{
  private final static Logger LOGGER = LoggerFactory.getLogger(DSBDataCache.class);
  private final Cache<String, DSBOrganization> _dsbOrganizationCache = CacheBuilder.newBuilder()
    .expireAfterWrite(12, TimeUnit.HOURS)
    .build();
  private final DSBOrganizationDAO _dsbOrganizationDAO;
  private final Cache<String, DSBPlayer> _dsbPlayerCache = CacheBuilder.newBuilder()
    .expireAfterWrite(12, TimeUnit.HOURS)
    .build();
  private final DSBPlayerDAO _dsbPlayerDAO;
  private final Cache<String, List<DSBPlayer>> _dsbPlayersCache = CacheBuilder.newBuilder()
    .expireAfterWrite(12, TimeUnit.HOURS)
    .build();
  private final Cache<String, List<DSBStats>> _dsbStatsCache = CacheBuilder.newBuilder()
    .expireAfterWrite(12, TimeUnit.HOURS)
    .build();

  public DSBDataCache(final DSBOrganizationDAO dsbOrganizationDAO_,
                      final DSBPlayerDAO dsbPlayerDAO_)
  {
    _dsbPlayerDAO = dsbPlayerDAO_;
    _dsbOrganizationDAO = dsbOrganizationDAO_;
  }

  public DSBOrganization getDSBOrganization(final String organizationId_)
  {
    try
    {
      return _dsbOrganizationCache.get(organizationId_, () -> {
        final List<DSBOrganization> organizations = _dsbOrganizationDAO.selectById(organizationId_);
        if (!organizations.isEmpty())
        {
          final DSBOrganization dsbOrganization = organizations.get(0);
          for (final DSBOrganization child : getChildrenForOrganization(dsbOrganization))
          {
            child.add(_dsbPlayerDAO.selectByClubId(child.getOrganizationId()));

            for (final DSBPlayer player : child.getPlayers())
            {
              player.setDWZ(_dsbPlayerDAO.selectDWZByPlayer(player));
              player.setFIDE(_dsbPlayerDAO.selectFIDEByPlayer(player));
            }
            dsbOrganization.add(child);
          }
          return dsbOrganization;
        }
        throw new ExecutionException(new Throwable("Not found"));
      });
    }
    catch (final ExecutionException ex_)
    {
      //ignore
    }
    return null;
  }

  public DSBPlayer getDSBPlayer(final String playerId_)
  {
    try
    {
      return _dsbPlayerCache.get(playerId_, () -> {
        //ZPS (xxxxx-yyy)
        if (playerId_.contains("-"))
        {
          final String[] ids = playerId_.split("-");
          final List<DSBPlayer> players = _dsbPlayerDAO.selectByClubIdAndMemberId(ids[0], ids[1]);
          if (!players.isEmpty())
          {
            final DSBPlayer player = players.get(0);
            setDWZandELO(player);
            return player;
          }
        }
        //DSBID (integer)
        else
        {
          final List<DSBPlayer> players = _dsbPlayerDAO.selectByDSBId(Integer.parseInt(playerId_));
          if (!players.isEmpty())
          {
            final DSBPlayer player = players.get(0);
            setDWZandELO(player);
            return player;
          }
        }
        throw new ExecutionException(new Throwable("Not found"));
      });
    }
    catch (final ExecutionException ex_)
    {
      //ignore
    }
    return null;
  }

  public List<DSBPlayer> getDSBPlayers(final String organizationId_)
  {
    try
    {
      return _dsbPlayersCache.get(organizationId_, () -> {
        final List<DSBPlayer> players = _dsbPlayerDAO.selectPlayersFor(organizationId_);
        for (final DSBPlayer dsbPlayer : players)
        {
          setDWZandELO(dsbPlayer);
        }
        return players;
      });
    }
    catch (final ExecutionException ex_)
    {
      //ignore
    }
    return new ArrayList<>();
  }

  public List<DSBStats> getStats(final String organizationId_)
  {
    try
    {
      return _dsbStatsCache.get(organizationId_, () -> {
        final List<DSBStats> clubDWZStats = _dsbPlayerDAO.selectDWZStatsFor(organizationId_);
        final List<DSBStats> dsbDWZStats = _dsbPlayerDAO.selectDWZStatsFor("00000");
        final List<DSBStats> clubELOStats = _dsbPlayerDAO.selectELOStatsFor(organizationId_);
        final List<DSBStats> dsbELOStats = _dsbPlayerDAO.selectELOStatsFor("00000");
        final List<DSBStats> clubMemberStats = _dsbPlayerDAO.selectMemberStatsFor(organizationId_);
        final List<DSBStats> membersWithoutDWZStats = _dsbPlayerDAO.selectMembersWithoutDWZByAge(organizationId_);
        final List<DSBStats> membersWithoutELOStats = _dsbPlayerDAO.selectMembersWithoutELOByAge(organizationId_);

        final Map<Integer, List<DSBStats>> stats = new HashMap<>();
        stats.put(DSBStats.CLUB_DWZ, clubDWZStats);
        stats.put(DSBStats.DSB_DWZ, dsbDWZStats);
        stats.put(DSBStats.CLUB_ELO, clubELOStats);
        stats.put(DSBStats.DSB_ELO, dsbELOStats);
        stats.put(DSBStats.CLUB_MEMBERS, clubMemberStats);
        stats.put(DSBStats.NO_DWZ_MEMBERS, membersWithoutDWZStats);
        stats.put(DSBStats.NO_ELO_MEMBERS, membersWithoutELOStats);
        return DSBStats.asConsolidatedStats(stats);
      });
    }
    catch (final ExecutionException ex_)
    {
      //ignore
    }
    return new ArrayList<>();
  }

  /**
   * recursive lookup
   **/
  private List<DSBOrganization> getChildrenForOrganization(final DSBOrganization organization_)
  {
    final List<DSBOrganization> organizations = _dsbOrganizationDAO.selectChildrenOf(organization_.getOrganizationId());
    for (final DSBOrganization organization : organizations)
    {
      organization.add(_dsbPlayerDAO.selectByClubId(organization.getOrganizationId()));
      getChildrenForOrganization(organization);
      organization_.add(organization);
    }
    return organizations;
  }


  private void setDWZandELO(final DSBPlayer player_)
  {
    final List<DWZ> dwzs = _dsbPlayerDAO.selectDWZByPlayer(player_);
    player_.setDWZ(dwzs);

    final List<FIDE> fides = _dsbPlayerDAO.selectFIDEByPlayer(player_);
    player_.setFIDE(fides);
  }

}
