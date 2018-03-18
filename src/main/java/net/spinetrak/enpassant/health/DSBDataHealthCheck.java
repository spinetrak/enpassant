package net.spinetrak.enpassant.health;

import com.codahale.metrics.health.HealthCheck;
import net.spinetrak.enpassant.configuration.DSBDataClient;

public class DSBDataHealthCheck extends HealthCheck
{
  final DSBDataClient _dsbDataClient;

  public DSBDataHealthCheck(final DSBDataClient dsbDataClient_)
  {
    _dsbDataClient = dsbDataClient_;
  }

  @Override
  protected Result check() throws Exception
  {
    if (_dsbDataClient.isUpToDate())
    {
      return Result.healthy();
    }
    else
    {
      return Result.unhealthy("DSB data is stale: " + _dsbDataClient.lastUpdate());
    }
  }
}
