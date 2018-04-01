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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DSBStats
{
  private final List<DSBStats> _data = new ArrayList<>();
  private Integer _age;
  private Float _dwz;
  private Float _dwzDSB;
  private Float _elo;
  private Float _eloDSB;
  private Map<Integer, Float[]> _stats;

  public DSBStats()
  {
    _stats = new HashMap<>();
    for (int i = 0; i <= 100; i++)
    {
      _stats.put(i, new Float[]{0f, 0f, 0f, 0f});
    }
  }

  public Integer getAge()
  {
    return _age;
  }

  @JsonIgnore
  public List<DSBStats> getData()
  {
    return _data;
  }

  public Float getDwz()
  {
    return _dwz;
  }

  public Float getDwzDSB()
  {
    return _dwzDSB;
  }

  public Float getElo()
  {
    return _elo;
  }

  public Float getEloDSB()
  {
    return _eloDSB;
  }

  public void setStats(final Map<Integer, Float[]> stats_)
  {
    if (null != stats_)
    {
      _stats.putAll(stats_);
    }

    for (final Map.Entry<Integer, Float[]> entry : _stats.entrySet())
    {
      final Integer age = entry.getKey();
      final Float[] stats = entry.getValue();

      final DSBStats DSBStats = new DSBStats();
      DSBStats.setAge(age);
      DSBStats.setDwzDSB(stats[0]);
      DSBStats.setDwz(stats[1]);
      DSBStats.setEloDSB(stats[2]);
      DSBStats.setElo(stats[3]);
      _data.add(DSBStats);
    }
  }

  private void setAge(final Integer age_)
  {
    _age = age_;
  }

  private void setDwz(final Float dwz_)
  {
    _dwz = dwz_;
  }

  private void setDwzDSB(final Float dwzDSB_)
  {
    _dwzDSB = dwzDSB_;
  }

  private void setElo(final Float elo_)
  {
    _elo = elo_;
  }

  private void setEloDSB(final Float eloDSB_)
  {
    _eloDSB = eloDSB_;
  }
}
