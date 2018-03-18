package net.spinetrak.enpassant.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.setup.Environment;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DSBDataFactory
{
  @NotNull
  private Integer refreshInterval;
  @NotEmpty
  private String url;

  public DSBDataClient build(final Environment environment_)
  {
    final ScheduledExecutorService ses = environment_.lifecycle().scheduledExecutorService("dsbDataClient").build();
    final DSBDataClient dsbDataClient = new DSBDataClient(getUrl());
    ses.scheduleWithFixedDelay(dsbDataClient, 3, getRefreshInterval(), TimeUnit.SECONDS);
    return dsbDataClient;
  }

  @JsonProperty
  public int getRefreshInterval()
  {
    return refreshInterval;
  }

  @JsonProperty
  public String getUrl()
  {
    return url;
  }

  @JsonProperty
  public void setRefreshInterval(final int refreshInterval_)
  {
    refreshInterval = refreshInterval_;
  }

  @JsonProperty
  public void setUrl(final String url_)
  {
    url = url_;
  }
}
