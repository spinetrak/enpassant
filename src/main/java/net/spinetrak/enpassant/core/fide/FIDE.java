package net.spinetrak.enpassant.core.fide;

public class FIDE
{
  private final String _country;
  private final Integer _elo;
  private final Integer _id;
  private final String _title;

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

  public String getTitle()
  {
    return _title;
  }

  public FIDE(final Integer id_, final Integer elo_, final String title_, final String country_)
  {
    _id = id_;
    _elo = elo_;
    _title = title_;

    _country = country_;
  }
}
