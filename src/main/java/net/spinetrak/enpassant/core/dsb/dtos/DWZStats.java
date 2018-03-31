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

public class DWZStats
{
  private final List<DWZStats> _data = new ArrayList<>();
  private Integer _age;
  private Integer _dwz;
  private Integer _dwzDSB;
  private Map<Integer, Integer[]> _stats;
  public DWZStats()
  {
    _stats = new HashMap<>();
    for (int i = 0; i <= 100; i++)
    {
      _stats.put(i, new Integer[]{0, 0});
    }
  }

  public Integer getAge()
  {
    return _age;
  }

  @JsonIgnore
  public List<DWZStats> getData()
  {
    return _data;
  }

  public Integer getDwz()
  {
    return _dwz;
  }

  public Integer getDwzDSB()
  {
    return _dwzDSB;
  }

  public void setDwz(final Integer dwz_)
  {
    _dwz = dwz_;
  }

  public void setStats(final Map<Integer, Integer[]> stats_)
  {
    if (null != stats_)
    {
      _stats.putAll(stats_);
    }

    for (final Map.Entry<Integer, Integer[]> entry : _stats.entrySet())
    {
      final Integer age = entry.getKey();
      final Integer[] dwz = entry.getValue();

      final DWZStats dwzStats = new DWZStats();
      dwzStats.setAge(age);
      dwzStats.setDwzDSB(dwz[0]);
      dwzStats.setDwz(dwz[1]);
      _data.add(dwzStats);
    }
  }

  private void setAge(final Integer age_)
  {
    _age = age_;
  }

  private void setDwzDSB(final Integer dwzDSB_)
  {
    _dwzDSB = dwzDSB_;
  }

}
