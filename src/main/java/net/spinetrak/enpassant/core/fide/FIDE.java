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

package net.spinetrak.enpassant.core.fide;


import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public class FIDE
{
  private String _country;
  private Integer _elo = 0;
  private Integer _id = 0;
  private Date _lastEval = Date.from(LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth()).atStartOfDay().toInstant(
    ZoneOffset.UTC));
  private String _title;

  public String getCountry()
  {
    return _country;
  }

  public Integer getElo()
  {
    return _elo;
  }

  public Integer getId()
  {
    return _id;
  }

  public Date getLastEval()
  {
    return new Date(_lastEval.getTime());
  }

  public String getTitle()
  {
    return _title;
  }

  public void setCountry(final String country_)
  {
    _country = country_;
  }

  public void setElo(final Integer elo_)
  {
    if (elo_ != null)
    {
      _elo = elo_;
    }
  }

  public void setId(final Integer id_)
  {
    if (id_ != null)
    {
      _id = id_;
    }
  }

  public void setLastEval(final Date lastEval_)
  {
    if (lastEval_ != null)
    {
      _lastEval = new Date(lastEval_.getTime());
    }
  }

  public void setTitle(final String title_)
  {
    _title = title_;
  }

  @Override
  public String toString()
  {
    return "FIDE{" +
      "country='" + _country + '\'' +
      ", elo=" + _elo +
      ", id=" + _id +
      ", title='" + _title + '\'' +
      ", lastEval=" + _lastEval +
      '}';
  }
}
