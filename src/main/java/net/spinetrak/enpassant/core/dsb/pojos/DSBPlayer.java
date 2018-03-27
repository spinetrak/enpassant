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

import net.spinetrak.enpassant.core.fide.FIDE;

import java.util.ArrayList;
import java.util.List;

public class DSBPlayer
{
  private final List<DWZ> _dwz = new ArrayList<>();
  private final List<FIDE> _fide = new ArrayList<>();
  private String _clubId;
  private int _dsbId;
  private String _eligibility;
  private Integer _fideId = -1;
  private String _gender;
  private String _memberId;
  private String _name;
  private String _status;
  private Integer _yob;

  public void addDWZ(final DWZ dwz_)
  {
    if (dwz_ != null)
    {
      _dwz.add(dwz_);
    }
  }

  public void addFIDE(final FIDE fide_)
  {
    if (fide_ != null)
    {
      _fide.add(fide_);
      if (_fideId == -1)
      {
        _fideId = fide_.getId();
      }
    }
  }

  public String getClubId()
  {
    return _clubId;
  }

  public List<DWZ> getDWZ()
  {
    return _dwz;
  }

  public int getDsbId()
  {
    return _dsbId;
  }

  public String getEligibility()
  {
    return _eligibility;
  }

  public List<FIDE> getFIDE()
  {
    return _fide;
  }

  public Integer getFideId()
  {
    return _fideId;
  }

  public String getGender()
  {
    return _gender;
  }

  public String getMemberId()
  {
    return _memberId;
  }

  public String getName()
  {
    return _name;
  }

  public String getStatus()
  {
    return _status;
  }

  public Integer getYoB()
  {
    return _yob;
  }

  public void setClubId(final String clubId_)
  {
    _clubId = clubId_;
  }

  public void setDWZ(final List<DWZ> dwz_)
  {
    if (dwz_ != null && dwz_.size() > 0)
    {
      _dwz.addAll(dwz_);
    }
  }

  public void setDsbId(final int dsbid_)
  {
    _dsbId = dsbid_;
  }

  public void setEligibility(final String eligibility_)
  {
    _eligibility = eligibility_;
  }

  public void setFIDE(final List<FIDE> fide_)
  {
    if (fide_ != null && fide_.size() > 0)
    {
      _fide.addAll(fide_);
      if (_fideId == -1)
      {
        _fideId = fide_.get(0).getId();
      }
    }
  }

  public void setFideId(final Integer fideId_)
  {
    _fideId = fideId_;
  }

  public void setGender(final String gender_)
  {
    _gender = gender_;
  }

  public void setMemberId(final String memberId_)
  {
    _memberId = memberId_;
  }

  public void setName(final String name_)
  {
    _name = name_;
  }

  public void setStatus(final String status_)
  {
    _status = status_;
  }

  public void setYoB(final Integer yob_)
  {
    _yob = yob_;
  }

  @Override
  public String toString()
  {
    return "DSBPlayer{" +
      "_dwz=" + _dwz +
      ", eligibility='" + _eligibility + '\'' +
      ", fide=" + _fide +
      ", gender='" + _gender + '\'' +
      ", memberId='" + _memberId + '\'' +
      ", name='" + _name + '\'' +
      ", status='" + _status + '\'' +
      ", yob=" + _yob +
      ", clubId='" + _clubId + '\'' +
      ", dsbId=" + _dsbId +
      ", fideId=" + _fideId +
      '}';
  }
}
