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

package net.spinetrak.enpassant.core.dsb.pojos;

import java.util.Date;


public class DWZ
{
  private String _clubId;
  private Integer _dwz;
  private Integer _index;
  private Date _lastEvaluation;
  private String _memberId;

  public String getClubId()
  {
    return _clubId;
  }

  public Integer getDwz()
  {
    return _dwz;
  }

  public Integer getIndex()
  {
    return _index;
  }

  public Date getLastEvaluation()
  {
    return _lastEvaluation != null ? new Date(_lastEvaluation.getTime()) : null;
  }

  public String getMemberId()
  {
    return _memberId;
  }

  public void setClubId(final String clubId_)
  {
    _clubId = clubId_;
  }

  public void setDwz(final Integer dwz_)
  {
    _dwz = dwz_;
  }

  public void setIndex(final Integer index_)
  {
    _index = index_;
  }

  public void setLastEvaluation(final Date lastEvaluation_)
  {
    _lastEvaluation = (lastEvaluation_ != null) ? new Date(lastEvaluation_.getTime()) : null;
  }

  public void setMemberId(final String memberId_)
  {
    _memberId = memberId_;
  }

  @Override
  public String toString()
  {
    return "DWZ{" +
      "dwz=" + _dwz +
      ", memberId='" + _memberId + '\'' +
      ", index=" + _index +
      ", lastEvaluation=" + _lastEvaluation +
      ", clubId='" + _clubId + '\'' +
      '}';
  }

}
