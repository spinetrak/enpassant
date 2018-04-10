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

package net.spinetrak.enpassant.core.dsb.dtos;

import java.util.*;

public class DSBPlayerStats
{
  private Integer _dwz;
  private Date _dwzLastEval;
  private Integer _elo;
  private Date _eloLastEval;

  public static List<DSBPlayerStats> consolidate(final List<DSBPlayerStats> stats_)
  {
    final Map<Date, DSBPlayerStats> map = new TreeMap<>();

    for (final DSBPlayerStats stat : stats_)
    {
      if (stat.getDwzLastEval() != null)
      {
        final DSBPlayerStats dwz = new DSBPlayerStats();
        dwz.setDWZLastEval(stat.getDwzLastEval());
        dwz.setDWZ(stat.getDwz());
        map.put(stat.getDwzLastEval(), dwz);
      }
      if (stat.getEloLastEval() != null)
      {
        final DSBPlayerStats elo = new DSBPlayerStats();
        elo.setELOLastEval(stat.getEloLastEval());
        elo.setELO(stat.getElo());
        map.put(stat.getEloLastEval(), elo);
      }
    }

    final List<DSBPlayerStats> stats = new ArrayList<>();
    DSBPlayerStats dwz = null;
    DSBPlayerStats elo = null;
    for (final Map.Entry<Date, DSBPlayerStats> entry : map.entrySet())
    {
      final DSBPlayerStats stat = entry.getValue();
      if (stat.getDwzLastEval() != null)
      {
        dwz = stat;
      }
      if (stat.getEloLastEval() != null)
      {
        elo = stat;
      }
      if (dwz != null)
      {
        stat.setDWZ(dwz.getDwz());
      }
      if (elo != null)
      {
        stat.setELO(elo.getElo());
      }
      stats.add(stat);
    }
    return stats;
  }

  public Integer getDwz()
  {
    return _dwz;
  }

  public Integer getElo()
  {
    return _elo;
  }

  public Date getLastEval()
  {
    return _dwzLastEval != null ? new Date(_dwzLastEval.getTime()) : _eloLastEval != null ? new Date(
      _eloLastEval.getTime()) : null;
  }

  public void setDWZ(final Integer dwz_)
  {
    _dwz = dwz_;
  }

  public void setDWZLastEval(final Date dwzLastEval_)
  {
    if (dwzLastEval_ != null)
    {
      _dwzLastEval = new Date(dwzLastEval_.getTime());
    }
  }

  public void setELO(final Integer elo_)
  {
    _elo = elo_;
  }

  public void setELOLastEval(final Date eloLastEval_)
  {
    if (eloLastEval_ != null)
    {
      _eloLastEval = new Date(eloLastEval_.getTime());
    }
  }

  private Date getDwzLastEval()
  {
    return _dwzLastEval;
  }

  private Date getEloLastEval()
  {
    return _eloLastEval;
  }
}
