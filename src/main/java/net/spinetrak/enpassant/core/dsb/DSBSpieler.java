package net.spinetrak.enpassant.core.dsb;

import net.spinetrak.enpassant.core.fide.FIDE;

import javax.validation.constraints.NotNull;

public class DSBSpieler
{
  private final DWZ _dwz;
  private final String _eligibility;
  private final FIDE _fide;
  private final Character _gender;
  private final String _id;
  private final String _name;
  private final String _status;
  private final DSBVerein _verein;
  private final Integer _yob;

  public DSBSpieler(@NotNull final String id_, final DSBVerein verein_, @NotNull final String name_,
                    final String status_,
                    final Character gender_, final String eligibility_, final Integer yob_, final DWZ dwz_,
                    final FIDE fide_)
  {
    _id = id_;
    _verein = verein_;
    _name = name_;
    _status = status_;
    _gender = gender_;
    _eligibility = eligibility_;
    _yob = yob_;
    _dwz = dwz_;
    _fide = fide_;
  }

  public DWZ getDwz()
  {
    return _dwz;
  }

  public String getEligibility()
  {
    return _eligibility;
  }

  public FIDE getFide()
  {
    return _fide;
  }

  public Character getGender()
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

  @Override
  public String toString()
  {
    return "DSBSpieler{" +
      "dwz=" + _dwz +
      ", eligibility='" + _eligibility + '\'' +
      ", fide=" + _fide +
      ", gender=" + _gender +
      ", id='" + _id + '\'' +
      ", name='" + _name + '\'' +
      ", status='" + _status + '\'' +
      ", verein=" + _verein +
      ", yob=" + _yob +
      '}';
  }
}
