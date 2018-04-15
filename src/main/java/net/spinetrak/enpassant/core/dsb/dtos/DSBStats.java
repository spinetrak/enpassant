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
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DSBStats
{
  public static final int CLUB_DWZ = 3;
  public static final int CLUB_ELO = 1;
  public static final int CLUB_MEMBERS = 5;
  public static final int DSB_DWZ = 4;
  public static final int DSB_ELO = 2;
  public static final int NO_DWZ_MEMBERS = 6;
  public static final int NO_ELO_MEMBERS = 7;
  private final static int TODAY = DateTime.now().getYear();
  private final List<DSBStats> _data = new ArrayList<>();
  private Integer _age = 0;
  private Float _avg = 0f;
  private Float _dwz = 0f;
  private Float _dwzDSB = 0f;
  private Float _elo = 0f;
  private Float _eloDSB = 0f;
  private Integer _members = 0;
  private Integer _membersWithoutDWZ = 0;
  private Integer _membersWithoutELO = 0;
  private Integer _yoB;

  public static List<DSBStats> asConsolidatedStats(final Map<Integer, List<DSBStats>> stats_)
  {
    final List<DSBStats> consolidated = new ArrayList<>();
    for (int x = 0; x < 100; x++)
    {
      final DSBStats consolidate = new DSBStats();
      consolidate.setAge(x);
      for (final Map.Entry<Integer, List<DSBStats>> stats : stats_.entrySet())
      {
        final Integer statsType = stats.getKey();
        final List<DSBStats> theStats = stats.getValue();
        for (final DSBStats stat : theStats)
        {
          if (stat != null && stat.getYoB() != null)
          {
            final int age = TODAY - stat.getYoB();
            final Float avg = stat.getAvg();
            final Integer mem = stat.getMembers();
            if (age == x)
            {
              switch (statsType)
              {
                case CLUB_ELO:
                  consolidate.setElo(avg);
                  break;
                case DSB_ELO:
                  consolidate.setEloDSB(avg);
                  break;
                case CLUB_DWZ:
                  consolidate.setDwz(avg);
                  break;
                case DSB_DWZ:
                  consolidate.setDwzDSB(avg);
                  break;
                case CLUB_MEMBERS:
                  consolidate.setMembers(mem);
                  break;
                case NO_DWZ_MEMBERS:
                  consolidate.setMembersWithoutDWZ(mem);
                  break;
                case NO_ELO_MEMBERS:
                  consolidate.setMembersWithoutELO(mem);
                  break;
                default:
                  break;
              }
            }
          }
        }
      }

      consolidated.add(consolidate);
    }
    return consolidated;
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

  public Integer getMembers()
  {
    return _members;
  }

  public Integer getMembersWithDWZ()
  {
    return _members - _membersWithoutDWZ;
  }

  public Integer getMembersWithELO()
  {
    return _members - _membersWithoutELO;
  }

  public void setAverage(final float avg_)
  {
    _avg = avg_;
  }

  public void setMembers(final int members_)
  {
    _members = members_;
  }

  public void setYoB(final int yoB_)
  {
    _yoB = yoB_;
  }

  private Float getAvg()
  {
    return _avg;
  }

  private Integer getYoB()
  {
    return _yoB;
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

  private void setMembersWithoutDWZ(final int membersWithoutDWZ_)
  {
    _membersWithoutDWZ = membersWithoutDWZ_;
  }

  private void setMembersWithoutELO(final int membersWithoutELO_)
  {
    _membersWithoutELO = membersWithoutELO_;
  }
}
