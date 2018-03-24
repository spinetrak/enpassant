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

import javax.validation.constraints.NotNull;
import java.util.Date;


public class DWZ
{
  private final String _clubId;
  private final Integer _dwz;
  private final Integer _index;
  private final Date _lastEvaluation;
  private final String _memberId;

  public DWZ(@NotNull final String clubId_, @NotNull final String memberId_, @NotNull final Integer dwz_,
             @NotNull final Integer index_, @NotNull final Date lastEvaluation_)
  {
    _clubId = clubId_;
    _memberId = memberId_;
    _dwz = dwz_;
    _index = index_;
    _lastEvaluation = new Date(lastEvaluation_.getTime());
  }

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
    return new Date(_lastEvaluation.getTime());
  }

  public String getMemberId()
  {
    return _memberId;
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
