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

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class DSBPlayer
{
  private final List<DWZ> _dwz = new ArrayList<>();
  private final String _eligibility;
  private final List<FIDE> _fide = new ArrayList<>();
  private final String _gender;
  private final String _id;
  private final String _name;
  private final String _status;
  private final Integer _yob;
  private final String _zps;
  private Integer _fideId;

  public DSBPlayer(final String zps_, @NotNull final String id_, @NotNull final String name_,
                   final String status_,
                   final String gender_, final String eligibility_, final Integer yob_, final DWZ dwz_,
                   final FIDE fide_)
  {
    this(zps_, id_, name_, status_, gender_, eligibility_, yob_, dwz_);

    if (fide_ != null)
    {
      _fideId = fide_.getId();
      _fide.add(fide_);
    }
    else
    {
      _fideId = -1;
    }
  }

  public DSBPlayer(final String zps_, final String member_, final String name_, final String status_,
                   final String gender_, final String eligibility_, final int yob_, final DWZ dwz_, final int fideId_)
  {
    this(zps_, member_, name_, status_, gender_, eligibility_, yob_, dwz_);
    _fideId = fideId_;
  }

  private DSBPlayer(final String zps_, final String id_, final String name_, final String status_,
                    final String gender_, final String eligibility_, final int yob_, final DWZ dwz_)
  {
    _zps = zps_;
    _id = id_.trim();
    _name = name_.trim();
    _status = status_;
    _gender = gender_;
    _eligibility = eligibility_;
    _yob = yob_;

    if (dwz_ != null)
    {
      _dwz.add(dwz_);
    }
  }

  public String getClub()
  {
    return _zps;
  }

  public List<DWZ> getDWZ()
  {
    return _dwz;
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

  public String getId()
  {
    return _id;
  }

  public String getName()
  {
    return _name;
  }

  public String getStatus()
  {
    return _status;
  }

  public Integer getYob()
  {
    return _yob;
  }

  public void setDWZ(final List<DWZ> dwz_)
  {
    _dwz.addAll(dwz_);
  }

  public void setFIDE(final List<FIDE> fide_)
  {
    _fide.addAll(fide_);
  }

  @Override
  public String toString()
  {
    return "DSBPlayer{" +
      "dwz=" + _dwz +
      ", eligibility='" + _eligibility + '\'' +
      ", fide=" + _fide +
      ", gender=" + _gender +
      ", id='" + _id + '\'' +
      ", name='" + _name + '\'' +
      ", status='" + _status + '\'' +
      ", club=" + _zps +
      ", yob=" + _yob +
      '}';
  }
}
