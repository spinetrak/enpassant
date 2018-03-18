package net.spinetrak.enpassant.core.dsb;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class DWZ
{
  private final Integer _dwz;
  private final Integer _index;
  private final Date _lastEvaluation;

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
    return _lastEvaluation;
  }

  public DWZ(@NotNull final Integer dwz_, @NotNull final Integer index_, @NotNull final Date lastEvaluation_)
  {
    _dwz = dwz_;
    _index = index_;

    _lastEvaluation = lastEvaluation_;
  }
}
