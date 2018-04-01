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

package net.spinetrak.enpassant.core.dsb.daos;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stats
{
  private final static int TODAY = DateTime.now().getYear();
  private Float _avg;
  private Integer _yoB;

  public static Map<Integer, Stats> asMap(final List<Stats> stats_)
  {
    final Map<Integer, Stats> map = new HashMap<>();
    if (stats_ == null)
    {
      return map;
    }
    for (final Stats stats : stats_)
    {
      if (stats != null && stats.getYoB() != null)
      {
        map.put(TODAY - stats.getYoB(), stats);
      }
    }
    return map;
  }

  public Float getAvg()
  {
    return _avg;
  }

  public Integer getYoB()
  {
    return _yoB;
  }

  public void setAverage(final float avg_)
  {
    _avg = avg_;
  }

  public void setYoB(final int yoB_)
  {
    _yoB = yoB_;
  }
}
