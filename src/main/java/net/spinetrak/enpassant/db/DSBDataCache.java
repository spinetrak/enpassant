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
import net.spinetrak.enpassant.core.dsb.daos.DSBAssociationDAO;
import net.spinetrak.enpassant.core.dsb.daos.DSBClubDAO;
import net.spinetrak.enpassant.core.dsb.daos.DSBPlayerDAO;
import net.spinetrak.enpassant.core.dsb.daos.Stats;
import net.spinetrak.enpassant.core.dsb.pojos.DSBAssociation;
import net.spinetrak.enpassant.core.dsb.pojos.DSBClub;
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
  private final Cache<String, DSBAssociation> _dsbAssociationCache = CacheBuilder.newBuilder()
    .expireAfterWrite(12, TimeUnit.HOURS)
    .build();
  private final DSBAssociationDAO _dsbAssociationDAO;
  private final Cache<String, DSBClub> _dsbClubCache = CacheBuilder.newBuilder()
    .expireAfterWrite(12, TimeUnit.HOURS)
    .build();
  private final DSBClubDAO _dsbClubDAO;
  private final Cache<String, DSBPlayer> _dsbPlayerCache = CacheBuilder.newBuilder()
    .expireAfterWrite(12, TimeUnit.HOURS)
    .build();
  private final DSBPlayerDAO _dsbPlayerDAO;
  private final Cache<String, List<DSBPlayer>> _dsbPlayersCache = CacheBuilder.newBuilder()
    .expireAfterWrite(12, TimeUnit.HOURS)
    .build();
  private final Cache<String, Map<Integer, Float[]>> _dsbStatsCache = CacheBuilder.newBuilder()
    .expireAfterWrite(12, TimeUnit.HOURS)
    .build();

  public DSBDataCache(final DSBAssociationDAO dsbAssociationDAO_, final DSBClubDAO dsbClubDAO_,
                      final DSBPlayerDAO dsbPlayerDAO_)
  {
    _dsbPlayerDAO = dsbPlayerDAO_;
    _dsbAssociationDAO = dsbAssociationDAO_;
    _dsbClubDAO = dsbClubDAO_;
  }

  public DSBAssociation getDSBAssociation(final String associationId_)
  {
    try
    {
      return _dsbAssociationCache.get(associationId_, () -> {
        final List<DSBAssociation> associations = _dsbAssociationDAO.select(associationId_);
        if (!associations.isEmpty())
        {
          final DSBAssociation dsbAssociation = associations.get(0);
          for (final DSBAssociation child : getChildrenForAssociation(dsbAssociation))
          {
            dsbAssociation.add(child);
          }
          return dsbAssociation;
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

  public DSBClub getDSBClub(final String clubId_)
  {
    try
    {
      return _dsbClubCache.get(clubId_, () -> {
        final List<DSBClub> clubs = _dsbClubDAO.select(clubId_);
        if (!clubs.isEmpty())
        {
          final DSBClub club = clubs.get(0);
          club.add(_dsbPlayerDAO.selectByClubId(club.getClubId()));

          for (final DSBPlayer player : club.getPlayers())
          {
            player.setDWZ(_dsbPlayerDAO.selectDWZByPlayer(player));
            player.setFIDE(_dsbPlayerDAO.selectFIDEByPlayer(player));
          }
          return club;
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
            final List<DWZ> dwzs = _dsbPlayerDAO.selectDWZByPlayer(player);
            player.setDWZ(dwzs);

            final List<FIDE> fides = _dsbPlayerDAO.selectFIDEByPlayer(player);
            player.setFIDE(fides);
            return player;
          }
        }
        //DSBID (integer)
        else
        {
          final List<DSBPlayer> players = _dsbPlayerDAO.selectByDSBId(playerId_);
          if (!players.isEmpty())
          {
            final DSBPlayer player = players.get(0);
            final List<DWZ> dwzs = _dsbPlayerDAO.selectDWZByPlayer(player);
            player.setDWZ(dwzs);

            final List<FIDE> fides = _dsbPlayerDAO.selectFIDEByPlayer(player);
            player.setFIDE(fides);
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

  public List<DSBPlayer> getDSBPlayers(final String clubOrAssociationId_)
  {
    try
    {
      return _dsbPlayersCache.get(clubOrAssociationId_, () -> _dsbPlayerDAO.selectPlayersFor(clubOrAssociationId_));
    }
    catch (final ExecutionException ex_)
    {
      //ignore
    }
    return new ArrayList<>();
  }

  public Map<Integer, Float[]> getStats(final String clubOrAssociationId_)
  {
    try
    {
      return _dsbStatsCache.get(clubOrAssociationId_, () -> {
        final List<Stats> clubStats = _dsbPlayerDAO.selectDWZsFor(clubOrAssociationId_);
        final List<Stats> dsbStats = _dsbPlayerDAO.selectDWZsFor("00000");
        final Map<Integer, Stats> club = Stats.asMap(clubStats);
        final Map<Integer, Stats> dsb = Stats.asMap(dsbStats);
        return mergeStats(club, dsb);
      });
    }
    catch (final ExecutionException ex_)
    {
      //ignore
    }
    return new HashMap<>();
  }


  /**
   * recursive lookup
   **/
  private List<DSBAssociation> getChildrenForAssociation(final DSBAssociation association_)
  {
    final List<DSBAssociation> associations = _dsbAssociationDAO.selectChildrenOf(association_.getAssociationId());
    for (final DSBAssociation association : associations)
    {
      getChildrenForAssociation(association);
      association_.add(association);
    }

    final List<DSBClub> clubs = _dsbClubDAO.selectChildrenOf(association_.getAssociationId());
    for (final DSBClub club : clubs)
    {
      club.add(_dsbPlayerDAO.selectByClubId(club.getClubId()));
      association_.add(club);
    }
    return associations;
  }

  private Map<Integer, Float[]> mergeStats(final Map<Integer, Stats> clubResults_,
                                           final Map<Integer, Stats> dsbResults_)
  {
    final Map<Integer, Float[]> results = new HashMap<>();
    for (int i = 0; i < 100; i++)
    {
      final Stats clubStats = clubResults_.get(i);
      final Stats dsbStats = dsbResults_.get(i);

      final Float club = clubStats == null ? 0 : clubStats.getAvg();
      final Float dsb = dsbStats == null ? 0 : dsbStats.getAvg();

      results.put(i, new Float[]{dsb, club});
    }
    return results;
  }


}
