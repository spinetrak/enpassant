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

import java.util.*;

public class DSBPlayer
{
  private final TreeSet<DWZ> _dwz = new TreeSet<>(new Comparator<DWZ>()
  {
    @Override
    public int compare(final DWZ dwz1_, final DWZ dwz2_)
    {
      if(dwz1_.toString().equals(dwz2_.toString()))
      {
        return 0;
      }
      return dwz1_.getIndex().compareTo(dwz2_.getIndex());
    }
  });
  private final TreeSet<FIDE> _fide = new TreeSet<>(new Comparator<FIDE>()
  {
    @Override
    public int compare(final FIDE fide1_, final FIDE fide2_)
    {
      if(fide1_.toString().equals(fide2_.toString()))
      {
        return 0;
      }
      return fide1_.getLastEval().compareTo(fide2_.getLastEval());
    }
  });
  private String _clubId;
  private Integer _currentDWZ;
  private Integer _currentELO;
  private Integer _dsbId = 0;
  private String _eligibility;
  private Integer _fideId = 0;
  private String _gender;
  private String _memberId;
  private String _name;
  private String _status;
  private Integer _yob = 0;

  public void addDWZ(final DWZ dwz_)
  {
    if (dwz_ != null && !dwz_.getDwz().equals(0))
    {
      _dwz.add(dwz_);
    }
    setCurrentDWZ();
  }

  private void setCurrentDWZ()
  {
    if (_dwz.isEmpty())
    {
      _currentDWZ = 0;
    }
    else
    {
      _currentDWZ = _dwz.last().getDwz();
    }
  }

  public void addFIDE(final FIDE fide_)
  {
    if (fide_ != null && !fide_.getId().equals(0) && !fide_.getElo().equals(0))
    {
      _fide.add(fide_);
      if (_fideId.equals(0))
      {
        _fideId = fide_.getId();
      }
    }
    setCurrentELO();
  }

  private void setCurrentELO()
  {
    if (_fide.isEmpty())
    {
      _currentELO = 0;
    }
    else
    {
      _currentELO = _fide.last().getElo();
    }
  }

  public String getClubId()
  {
    return _clubId;
  }

  public Integer getCurrentDWZ()
  {
    return _currentDWZ;
  }

  public Integer getCurrentELO()
  {
     return _currentELO;
  }

  public Set<DWZ> getDWZ()
  {
    return _dwz;
  }

  public Integer getDsbId()
  {
    return _dsbId;
  }

  public String getEligibility()
  {
    return _eligibility;
  }

  public Set<FIDE> getFIDE()
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
      for (final DWZ dwz : dwz_)
      {
        addDWZ(dwz);
      }
    }
    else
    {
      _dwz.clear();
    }
  }

  public void setDsbId(final Integer dsbid_)
  {
    if (dsbid_ != null && !dsbid_.equals(0))
    {
      _dsbId = dsbid_;
    }
  }

  public void setEligibility(final String eligibility_)
  {
    _eligibility = eligibility_;
  }

  public void setFIDE(final List<FIDE> fide_)
  {
    if (fide_ != null && fide_.size() > 0)
    {
      for (final FIDE fide : fide_)
      {
        addFIDE(fide);
      }
    }
    else
    {
      _fide.clear();
    }
  }

  public void setFideId(final Integer fideId_)
  {
    if (fideId_ != null && !fideId_.equals(0))
    {
      _fideId = fideId_;
    }
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
    if (yob_ != null && !yob_.equals(0))
    {
      _yob = yob_;
    }
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
